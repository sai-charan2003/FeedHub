package com.example.rss_parser.viewmodel


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rss_parser.Utils.AISummaryState
import com.example.rss_parser.BuildConfig
import com.example.rss_parser.Utils.DistinctLinkForGoogleNewsState
import com.example.rss_parser.Utils.GoogleNews
import com.example.rss_parser.Utils.ProcessState
import com.example.rss_parser.Utils.SharedPref
import com.example.rss_parser.database.Implementation.BookmarkRepoImp
import com.example.rss_parser.database.Implementation.feedDAOImp
import com.example.rss_parser.database.feeddatabase.AppDatabase
import com.example.rss_parser.database.feeddatabase.Bookmarks
import com.example.rss_parser.database.feeddatabase.feeds
import com.example.rss_parser.database.feeddatabase.supabaseWebsite
import com.example.rss_parser.database.feeddatabase.websiteTileAndLink
import com.example.rss_parser.database.feeddatabase.websiteTitleAndFavicon
import com.example.rss_parser.rssdata.RssData
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.supabase.database.auto_update
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.prof18.rssparser.RssParserBuilder
import com.prof18.rssparser.model.RssChannel
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.SignOutScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.postgrest.from
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dankito.readability4j.Readability4J
import net.dankito.readability4j.extended.Readability4JExtended
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.UUID


class viewmodel(context: Context):ViewModel() {
    private val repo: feedDAOImp

    private val bookmarkRepoImp : BookmarkRepoImp
    val allfeeds: LiveData<List<feeds>>
    private var _allwebsites= MutableStateFlow(emptyList<String?>())
    val allwebsites=_allwebsites.asStateFlow()
    private var _distinctWebsiteTitles= MutableStateFlow(emptyList<websiteTitleAndFavicon?>())
    val distinctWebsiteTitles=_distinctWebsiteTitles.asStateFlow()
    private var _distinctWebsiteData= MutableStateFlow(emptyList<websiteTileAndLink?>())
    val distinctWebsiteData=_distinctWebsiteData.asStateFlow()


    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.apiKey
    )
    val SharedPref= SharedPref(context)
    val isAnonymousSignin=SharedPref.isAnonymousSignin
    private val _bookmarkdata = MutableStateFlow(emptyList<Bookmarks>())
    val bookmarkData= _bookmarkdata.asStateFlow()



    init {
        val feedRepository = AppDatabase.getDatabase(context).feedRepository()

        val bookmarkRepo=AppDatabase.getDatabase(context).BookmarkRepo()
        repo = feedDAOImp(feedRepository)

        bookmarkRepoImp=BookmarkRepoImp(bookmarkRepo)
        allfeeds = repo.allfeeds
        viewModelScope.launch {
            repo.SelectDistinctWebsiteTiiles().collectLatest {
                _distinctWebsiteTitles.tryEmit(it)
            }
        }
        viewModelScope.launch {
            bookmarkRepoImp.allBookmarks.collectLatest {

                _bookmarkdata.tryEmit(it)
            }
        }
        viewModelScope.launch {
            repo.selectDistinctWebsiteTitleAndLink().collectLatest {
                _distinctWebsiteData.tryEmit(it)
            }
        }
        viewModelScope.launch {
            repo.selectDistinctWebsites().collectLatest {
                _allwebsites.tryEmit(it)
            }
        }

    }


    private val _searchresults = MutableStateFlow<List<feeds>>(emptyList())
    val searchresults: Flow<List<feeds>> = _searchresults




    fun insert(feeds: feeds) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(feeds)
    }
    suspend fun getfeedbyid(id: Int): feeds {
        return withContext(Dispatchers.IO) {
            repo.getfeedbyid(id)
        }

    }

    fun updateIsWebsiteFav(isWebsiteFav:Boolean,websiteTitle:String) = viewModelScope.launch(Dispatchers.IO){
        repo.updateIsWebsiteFav(isWebsiteFav, websiteTitle)

    }


    fun search(query: String) {
        if (query.isEmpty()) {
            viewModelScope.launch {
                _searchresults.emit(emptyList())
            }

        } else {


            viewModelScope.launch(Dispatchers.IO) {
                repo.search("*$query*").collect {
                    _searchresults.emit(it)
                }
            }
        }


    }





    fun update(feeds: feeds) = viewModelScope.launch(Dispatchers.IO) {
        repo.update(feeds)
    }

    fun delete(website: String) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteFeedsByWebsite(website)
    }



    fun insertBookmarkInLocalDBWithID(id:Int)=viewModelScope.launch(Dispatchers.IO){
        val feedData = repo.getfeedbyid(id)
        val user =
            supabaseclient.client.auth.retrieveUserForCurrentSession(
                updateSession = true
            )

        bookmarkRepoImp.insert(
            Bookmarks(
            id = null,
            websitelink = feedData.feedlink,
            email = user.email,
            images = feedData.imageurl,
            title = feedData.feedtitle,
            website = feedData.website,
            date = feedData.date,
            websiteTitle = feedData.websiteTitle,
            websiteFavicon = feedData.websiteFavicon
        )
        )
    }
    fun insertBookmarkInLocalDB(bookmarks: Bookmarks)=viewModelScope.launch(Dispatchers.IO){
        bookmarkRepoImp.insert(bookmarks)
    }
    fun deleteBookmarkFromLocalDB(feedlink: String)=viewModelScope.launch(Dispatchers.IO){
        bookmarkRepoImp.deleteBookmark(feedlink)
    }


    fun clearAllBookmarks()=viewModelScope.launch(Dispatchers.IO) {
        bookmarkRepoImp.clearBookmarks()
    }


    fun clearsupabasedata()=viewModelScope.launch(Dispatchers.IO) {
        val user =
            supabaseclient.client.auth.retrieveUserForCurrentSession(
                updateSession = true
            )
        if(!isAnonymousSignin) {
            try {
                supabaseclient.client.from("website")
                    .delete {
                        filter {
                            eq("email", user.email!!)
                        }
                    }
            } catch (e: Exception) {
                Log.e("TAG", "clearwebsites: $e")
            }
        }

    }

    private var _isloading = MutableStateFlow(true)
    var isLoading = _isloading.asStateFlow()
    private var _urlsloaded = MutableStateFlow(false)

    private val _websiteUrls = MutableLiveData<List<String>>()


    private val _updatedata = MutableLiveData<List<auto_update>>()
    val updatedata: LiveData<List<auto_update>> = _updatedata

    private val builder = RssParserBuilder(
        callFactory = OkHttpClient(),
        charset = Charsets.UTF_8,
    )


    private val rssParser = builder.build()

    val rssData = MutableLiveData<RssData>()


    suspend fun fetchrssfeed(urls: String): RssChannel? {

        return withContext(Dispatchers.IO) {
            try {
                rssParser.getRssChannel(urls)

            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    fun setloading(loading : Boolean){
        _isloading.value=loading
    }

    fun delete(feeds: feeds) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(feeds)
    }

    fun cleardb() = viewModelScope.launch(Dispatchers.IO) {
        repo.clear()

    }





    suspend fun getRSSData(urls: List<String>)  : LiveData<ProcessState>{
        val processState= MutableLiveData<ProcessState>(ProcessState.Loading)

        withContext(Dispatchers.IO) {
            for (url in urls) {
                Log.d("TAG", "getRSSData: $url")
                if (url.contains("news.google.com")) {

                    val rssFeed = withContext(Dispatchers.IO) {
                        rssParser.getRssChannel(url)
                    }
                    val sourceURL = rssFeed.items.map { it.sourceUrl }
                    getRssDataForGoogleNews(url, sourceURL[0]!!)

                } else {

                    try {
                        val rssFeed = withContext(Dispatchers.IO) {
                            rssParser.getRssChannel(url)
                        }
                        val title = rssFeed.title
                        val FavIcon = getFeedSourceLogoUrl(rssFeed.link!!)


                        rssFeed.items.forEach { item ->

                            repo.insert(
                                feeds(
                                    id = 0,
                                    feedlink = item.link,
                                    feedtitle = item.title,
                                    date = item.pubDate,
                                    imageurl = item.image,
                                    opened = "false",
                                    website = url,
                                    description = item.description,
                                    websiteTitle = title,
                                    websiteFavicon = FavIcon
                                )
                            )
                        }
                        processState.postValue(ProcessState.Success)

                    } catch (e: Exception) {
                        processState.postValue(ProcessState.Error(e.message?.substringBefore("URL")!!))
                        e.printStackTrace()
                    }
                }
            }
        }


            return processState

    }
    suspend fun getDistinctSourceURL(website:String) : LiveData<DistinctLinkForGoogleNewsState>{
        val processState=MutableLiveData<DistinctLinkForGoogleNewsState>(DistinctLinkForGoogleNewsState.Loading)
        val URL="https://news.google.com/rss/search?q=$website&hl=en-IN&gl=IN&ceid=IN:en"
        try{
            val rssFeed = withContext(Dispatchers.IO) {
                rssParser.getRssChannel(URL)
            }
            val googleNewsData = rssFeed.items
                .distinctBy { it.sourceUrl }
                .map { item ->
                    GoogleNews(item.sourceUrl, item.sourceName)
                }
            processState.postValue(DistinctLinkForGoogleNewsState.Success(googleNewsData))

        } catch (e:Exception){
            processState.postValue(DistinctLinkForGoogleNewsState.Error(e.message!!.substringBefore("URL")))


        }
        return processState
    }

    fun getRssDataForSingleWebsite(feedlink: String)=viewModelScope.launch{
        try {
            val rssFeed = withContext(Dispatchers.IO) {
                rssParser.getRssChannel(feedlink)
            }
            val title=rssFeed.title
            val FavIcon=getFeedSourceLogoUrl(rssFeed.link!!)


            rssFeed.items.forEach { item ->
                    repo.insert(
                        feeds(
                            id = 0,
                            feedlink = item.link,
                            feedtitle = item.title,
                            date = item.pubDate,
                            imageurl = item.image,
                            opened = "false",
                            website = feedlink,
                            description = item.description,
                            websiteTitle = title,
                            websiteFavicon = FavIcon
                        )
                    )

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    fun getRssDataForGoogleNews(feedlink: String,sourceURL:String)=viewModelScope.launch{
        try {
            val rssFeed = withContext(Dispatchers.IO) {
                rssParser.getRssChannel(feedlink)
            }

            val title=rssFeed.title?.substringBefore("\" - Google News")?.substringAfter("\"www.")?.substringAfter("\"")?.trim()
            val FavIcon=getFeedSourceLogoUrl(sourceURL)


            rssFeed.items.forEach { item ->
                repo.insert(
                    feeds(
                        id = 0,
                        feedlink = item.link,
                        feedtitle = item.title,
                        date = item.pubDate,
                        imageurl = item.image,
                        opened = "false",
                        website = feedlink,
                        description = item.description,
                        websiteTitle = title,
                        websiteFavicon = FavIcon
                    )
                )

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getFeedWebsitesFromSupabase(): LiveData<ProcessState> {

        val processState= MutableLiveData<ProcessState>(ProcessState.Loading)
        if(!isAnonymousSignin) {
            try {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        var fetchedUrls =
                            supabaseclient
                                .client
                                .from("website")
                                .select()
                                .decodeList<supabaseWebsite>()
                        val fetchedWebsiteLink = fetchedUrls.map { it.websiteLink }
                        if (fetchedUrls.isNotEmpty()) {
                            _allwebsites.value.forEach {
                                if (!fetchedWebsiteLink.contains(it)) {
                                    repo.deleteFeedsByWebsite(it!!)
                                }
                            }

                        }
                        val state=getRSSData(fetchedWebsiteLink)
                        processState.postValue(state.value)
                    }
                }

            } catch (e: Exception) {
                processState.postValue(ProcessState.Error(e.message.toString()))
                Log.d("TAG", "getwebsiteurlfromdb: ${e.message}")
            }
        }
        else{
            processState.postValue(ProcessState.Success)
        }
        return processState

    }



    suspend fun getBookmarkDataFromSupabase() : LiveData<ProcessState>{
        val processState=MutableLiveData<ProcessState>(ProcessState.Loading)

        if(!isAnonymousSignin) {
            withContext(Dispatchers.IO) {
                try {
                    val bookmarksdata = supabaseclient.client.from("bookmarks").select()
                        .decodeList<Bookmarks>()
                    bookmarksdata.forEach {
                        insertBookmarkInLocalDB(it)
                    }
                    processState.postValue(ProcessState.Success)


                } catch (e: Exception) {
                    processState.postValue(ProcessState.Error(e.message?.substringBefore("URL")!!))
                    Log.d("TAG", "getbookmarksdata: $e")


                }


            }
        } else{
            processState.postValue(ProcessState.Success)
        }
        return processState

    }

    fun getAutoUpdateDataFromSupabase() {
        var updatedata: List<auto_update>
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    updatedata =
                        supabaseclient.client.from("auto_update").select().decodeList<auto_update>()

                }
                //Log.d("TAG", "getAutoUpdateDataFromSupabase: $updatedata")
                _updatedata.postValue(updatedata)
            }
        } catch (e: Exception) {
            Log.d("TAG", "getAutoUpdateDataFromSupabase: $e")
        }

    }



    fun LoginWithGoogle(context:Context) : LiveData<ProcessState>{
        val GoogleAuth = MutableLiveData<ProcessState>(ProcessState.Loading)

            viewModelScope.launch {

                val credentialManager = CredentialManager.create(context)
                val rowNonce = UUID.randomUUID().toString()
                val bytes = rowNonce.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it ->
                    str + "%02x".format(it)

                }
                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.googleId)
                    .setNonce(hashedNonce)
                    .build()
                val request: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                try{


                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credentaial = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentaial.data)

                val googleIdToken = googleIdTokenCredential.idToken
                supabaseclient.client.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rowNonce


                }
                    GoogleAuth.postValue(ProcessState.Success)
            }
                catch (e:Exception){
                    GoogleAuth.postValue(ProcessState.Error(e.message.toString()))
                }
        }
        return GoogleAuth


    }

    fun checkForAuthentication() :LiveData<ProcessState> {
        var loginstate = MutableLiveData<ProcessState>(ProcessState.Loading)
        viewModelScope.launch {


            supabaseclient.client.auth.sessionStatus.collect {
                when (it) {

                    is SessionStatus.Authenticated -> {
                        loginstate.postValue(ProcessState.Success)
                    }

                    SessionStatus.LoadingFromStorage -> {

                        loginstate.postValue(ProcessState.Error("It's problem from our end please try after sometime"))
                    }

                    SessionStatus.NetworkError -> {

                        loginstate.postValue(ProcessState.Error("Network Error"))
                    }

                    is SessionStatus.NotAuthenticated -> {
                        loginstate.postValue(ProcessState.Error("Not authenticated please try again"))

                    }

                }
            }

        }
        return loginstate
    }

    fun SignInWithOTP(useremail:String):LiveData<ProcessState>{
        val AuthState = MutableLiveData<ProcessState>(ProcessState.Loading)
        viewModelScope.launch {
            try{
                supabaseclient.client.auth.signInWith(OTP){
                    email=useremail

                }
                AuthState.postValue(ProcessState.Success)
                
            } catch (e:Exception){
                when(e){
                    is RestException->{
                        val error=e.message?.substringBefore("URL")
                        AuthState.postValue(ProcessState.Error(error.toString()))
                    }
                    
                }
            }
        }
        return AuthState

    }

    fun Updateopened(id:Int,opened:String){
        viewModelScope.launch {
            Log.d("TAG", "Updateopened: $opened")
            repo.UpdateOpened(id, opened)
        }


    }
    fun UpdateBookmarksInSupaBase(id:Int) : LiveData<ProcessState>{

        val Bookmarkstate= MutableLiveData<ProcessState>(ProcessState.Loading)
        if(!isAnonymousSignin) {
            viewModelScope.launch(Dispatchers.IO) {
                val feedData = repo.getfeedbyid(id)
                try {

                    val user =
                        supabaseclient.client.auth.retrieveUserForCurrentSession(
                            updateSession = true
                        )
                    supabaseclient.client.from("bookmarks")
                        .insert(
                            Bookmarks(
                                id = null,
                                websitelink = feedData.feedlink,
                                email = user.email,
                                images = feedData.imageurl,
                                title = feedData.feedtitle,
                                website = feedData.website,
                                date = feedData.date,
                                websiteTitle = feedData.websiteTitle,
                                websiteFavicon = feedData.websiteFavicon
                            )
                        )
                    Bookmarkstate.postValue(ProcessState.Success)
                } catch (e: Exception) {
                    when (e) {
                        is RestException -> {
                            Bookmarkstate.postValue(
                                ProcessState.Error(
                                    error = e.message!!.substringBefore(
                                        "URL"
                                    )
                                )
                            )
                        }
                    }
                }

            }
        } else{
            Bookmarkstate.postValue(ProcessState.Success)
        }
        return Bookmarkstate
    }
    fun DeleteBookmarkFromSupabase(feedlink:String): LiveData<ProcessState>{
        val bookmarkState= MutableLiveData<ProcessState>(ProcessState.Loading)
        if(!isAnonymousSignin) {
            viewModelScope.launch {
                try {
                    supabaseclient.client.from(
                        "bookmarks"
                    ).delete {
                        filter {

                            eq(
                                "websitelink",
                                feedlink
                            )

                        }
                    }
                    bookmarkState.postValue(ProcessState.Success)

                } catch (e: Exception) {
                    when (e) {
                        is RestException -> {
                            bookmarkState.postValue(
                                ProcessState.Error(
                                    e.message!!.substringBefore(
                                        "URL"
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
        else{
            bookmarkState.postValue(ProcessState.Success)
        }
        return bookmarkState
    }

    fun VerifyOTP(otp:String, useremail: String):  LiveData<ProcessState>{
        val AuthState =MutableLiveData<ProcessState>(ProcessState.Loading)
        viewModelScope.launch {
            try {
                supabaseclient.client.auth.verifyEmailOtp(
                    type = OtpType.Email.EMAIL,
                    email = useremail,
                    token = otp
                )
                AuthState.postValue(ProcessState.Success)
            } catch (e:Exception){
                when(e){
                    is RestException->{
                        AuthState.postValue(ProcessState.Error(e.message!!.substringBefore("URL")))

                    }
                }
            }
        }
        return AuthState

    }

    fun GenerateAISummary(websiteLink:String): LiveData<AISummaryState>{
        val aiSummaryState=MutableLiveData<AISummaryState>(AISummaryState.Loading)
        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(websiteLink)
                aiSummaryState.postValue(AISummaryState.Success(response.text!!))
            } catch (e:Exception){
                aiSummaryState.postValue(AISummaryState.Error(e.message!!))

            }

        }
        return aiSummaryState


    }

    fun getCurrentSessionToken(context:Context){
        try {
            supabaseclient.client.auth.currentAccessTokenOrNull()
        } catch (e: Exception) {
            when (e) {
                is RestException -> {
                    val error = e.message?.substringBefore("URL")
                    Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                }

                is HttpRequestTimeoutException -> {
                    val error = e.message?.substringBefore("URL")
                    Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                }

                is HttpRequestException -> {
                    val error = e.message?.substringBefore("URL")
                    Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun InsertWebsiteIntoSupaBase(feedlink:String,context:Context):LiveData<ProcessState> {
        val addWebsiteState = MutableLiveData<ProcessState>(ProcessState.Loading)
        if(!isAnonymousSignin) {
            viewModelScope.launch {
                try {

                    val user =
                        supabaseclient.client.auth.retrieveUserForCurrentSession(
                            updateSession = true
                        )
                    supabaseclient.client.from("website").insert(
                        supabaseWebsite(
                            id = null,
                            websiteLink = feedlink,
                            email = user.email
                        )
                    )
                    addWebsiteState.postValue(ProcessState.Success)


                } catch (e: Exception) {
                    addWebsiteState.postValue(e.message?.substringBefore("URL")
                        ?.let { ProcessState.Error(it) })
                }
            }
        }
        else{
            addWebsiteState.postValue(ProcessState.Success)
        }
        return addWebsiteState
    }
    fun deleteWebsiteFromSupabase(feedlink:String,context:Context):LiveData<ProcessState>{
        val addWebsiteState=MutableLiveData<ProcessState>(ProcessState.Loading)
        if(!isAnonymousSignin) {
            viewModelScope.launch {
                try {
                    supabaseclient.client.from("website").delete {
                        filter {
                            eq("websiteLink", feedlink)
                        }
                    }
                    addWebsiteState.postValue(ProcessState.Success)
                } catch (e: Exception) {
                    addWebsiteState.postValue(ProcessState.Error(e.message!!.substringBefore("URL")))
                }
            }
        }
        else{
            addWebsiteState.postValue(ProcessState.Success)
        }
        return addWebsiteState
    }

    fun Logout(): LiveData<ProcessState>{
        val processState=MutableLiveData<ProcessState>(ProcessState.Loading)
        viewModelScope.launch {
            try {
                supabaseclient.client.auth.signOut(SignOutScope.GLOBAL)
                processState.postValue(ProcessState.Success)
            } catch (e:Exception){
                processState.postValue(ProcessState.Error(e.message!!.substringBefore("URL")))
            }



        }
        return processState
    }

    @OptIn(SupabaseExperimental::class)
    fun anonymousSignIn(): LiveData<ProcessState>{
        val processState=MutableLiveData<ProcessState>(ProcessState.Loading)
        viewModelScope.launch {
            try {
                supabaseclient.client.auth.signInAnonymously()
                processState.postValue(ProcessState.Success)
            } catch (e: Exception) {
                processState.postValue(ProcessState.Error(e.message!!.substringBefore("URL")))

            }
        }
        return processState

    }

    fun fetchWebsitesFromSupabaseAndStoreInLocalDB()=viewModelScope.launch{
        _isloading.value=true
        withContext(Dispatchers.IO) {
            getFeedWebsitesFromSupabase()
            getBookmarkDataFromSupabase()
        }
        _isloading.value=false
    }

    fun getFeedSourceLogoUrl(feedlink:String): String? {

        val domain = Url(feedlink)

        val host =
            if (domain.host != "localhost") {
                domain.host
            } else {
                throw NullPointerException("Unable to get host domain")
            }

            return "https://api.statvoo.com/favicon/$host"

    }
    fun getImageUrlFromWebsite(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageUrl = withContext(Dispatchers.IO) {
                    val websiteurl=getFinalRedirectedUrl(url)
                    val htmlbody=Jsoup.connect(websiteurl).get().body()


                    val readibility= Readability4J(websiteurl!!,htmlbody.toString())
                    val readibilityex= Readability4JExtended(websiteurl!!,htmlbody.toString())
                    val article=readibility.parse()
                    val response = Jsoup.connect(url).get()
                    val document=Jsoup.parse(response.html())

                    val metaTags = document.getElementsByTag("meta")
                    val imageUrls = mutableListOf<String>()
                    for (tag in metaTags) {
                        if (tag.attr("property").startsWith("og:image")) {
                            imageUrls.add(tag.attr("content"))
                        }
                    }
                    Log.d("TAG", "getImageUrlFromWebsite: $imageUrls")

                    Log.d("TAG", "getImageUrlFromWebsite: $document")

                }
                if (imageUrl != null) {
                    Log.d("TAG", "getImageUrlFromWebsite: $imageUrl")
                } else {

                }
            } catch (e: IOException) {

            }
        }
    }
    suspend fun getFinalRedirectedUrl(url: String):String? {
        var websiteLink:String=""
        withContext(Dispatchers.IO) {
            val response=Jsoup.connect(url).get()

            val doc=Jsoup.parse(response.body().html())
            val links = doc.select("a[href]")

            for (link in links) {
                websiteLink = link.attr("href")
                Log.d("TAG", "getFinalRedirectedUrl: $websiteLink")
            }


        }
        return websiteLink
    }












}

