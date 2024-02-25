package com.example.rss_parser.viewmodel

import android.content.Context

import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rss_parser.database.feeddatabase.feeddatabase
import com.example.rss_parser.database.feeddatabase.feedrepo
import com.example.rss_parser.database.feeddatabase.feeds
import com.example.rss_parser.supabase.database.bookmarkdatabase


import com.example.rss_parser.rssdata.RssData
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.supabase.database.auto_update
import com.example.rss_parser.supabase.database.website_supabase
import com.example.rss_parser.database.feeddatabase.websitedatabase.websitedatabase
import com.example.rss_parser.database.feeddatabase.websitedatabase.websiterepo
import com.example.rss_parser.database.feeddatabase.websitedatabase.websites
import com.prof18.rssparser.RssParserBuilder
import com.prof18.rssparser.model.RssChannel
import io.github.jan.supabase.postgrest.from

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.IOException
import kotlin.math.log

class viewmodel(context: Context):ViewModel() {
    private val repo: feedrepo
    private val websiterepo: websiterepo
    val allfeeds:LiveData<List<feeds>>
    val allwebsites:LiveData<List<websites>>


    init {
        val dao= feeddatabase.getDatabase(context).feeddao()
        val websitedao= websitedatabase.getDatabase(context).websitedao()
        repo= feedrepo(dao)
        websiterepo= websiterepo(websitedao)
        allfeeds=repo.allfeeds
        allwebsites=websiterepo.allwebsites
    }

    private val _searchresults = MutableStateFlow<List<feeds>>(emptyList())
    val searchresults: Flow<List<feeds>> = _searchresults



    fun insert(feeds: feeds)=viewModelScope.launch(Dispatchers.IO) {
        repo.insert(feeds)
    }
    fun search(query: String) {
        if(query.isEmpty()){
            viewModelScope.launch {
                _searchresults.emit(emptyList())
            }

        }
        else {


            viewModelScope.launch(Dispatchers.IO) {
                repo.search("*$query*").collect {
                    _searchresults.emit(it)
                }
            }
        }


    }
    fun websiteinsert(websites: websites)=viewModelScope.launch(Dispatchers.IO) {
        websiterepo.insert(websites)
    }
    fun websitedelete(websitelink:String)=viewModelScope.launch(Dispatchers.IO) {
        websiterepo.delete(websitelink)
    }

    fun update(feeds: feeds)=viewModelScope.launch(Dispatchers.IO) {
        repo.update(feeds)
    }
    fun delete(website:String)=viewModelScope.launch(Dispatchers.IO) {
        repo.deleteFeedsByWebsite(website)
    }
    fun clearwebsites()=viewModelScope.launch(Dispatchers.IO) {
        websiterepo.clear()
    }

    private var _isloading= MutableStateFlow(true)
    var isLoading=_isloading.asStateFlow()
    private var _urlsloaded= MutableStateFlow(false)
    var urlsloaded=_urlsloaded.asStateFlow()
    var websiterb = mutableListOf<website_supabase>()
    private val _websiteUrls = MutableLiveData<List<String>>()
    val websiteUrls: LiveData<List<String>> = _websiteUrls
    private val _websiteforupdate = MutableLiveData<List<String>>()
    private val _bookmarkdata = MutableLiveData<List<bookmarkdatabase>>()
    val bookmarkdata: LiveData<List<bookmarkdatabase>> = _bookmarkdata
    private val _updatedata = MutableLiveData<List<auto_update>>()
    val updatedata: LiveData<List<auto_update>> = _updatedata

    private val builder = RssParserBuilder(
        callFactory = OkHttpClient(),
        charset = Charsets.UTF_8,
    )





    private val rssParser = builder.build()

        val rssData = MutableLiveData<RssData>()


     suspend fun fetchrssfeed(urls: String): RssChannel? {
         _isloading.value=true
        return withContext(Dispatchers.IO){
            try{

                rssParser.getRssChannel(urls)


            } catch (e:IOException){
                e.printStackTrace()
                null
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    fun delete(feeds: feeds)=viewModelScope.launch (Dispatchers.IO){
        repo.delete(feeds)
    }
    fun cleardb()=viewModelScope.launch(Dispatchers.IO){
        repo.clear()

    }
    fun getWebsiteLinks(): List<String> {
        val websites = websiterepo.allwebsites.value
        if (websites != null) {
            return websites.map {
                it.websitelink
            }
        }
        return emptyList()

    }

    fun updatefeeddatabase(){
        var websitelink=getWebsiteLinks()
        getData(websitelink)
        websitelink= emptyList()
    }


    fun getData(urls: List<String>) = viewModelScope.launch {
        _isloading.value=true
        val fetchedLinks = mutableListOf<String>()
        val fetchedTitles = mutableListOf<String>()
        val fetchedImages = mutableListOf<String>()
        val fetchedDates = mutableListOf<String>()

        for (url in urls) {
            try {
                val rssFeed = withContext(Dispatchers.IO) {
                    rssParser.getRssChannel(url)
                }
                Log.d("check", "getData: $rssFeed")




                rssFeed.items.forEach { item ->

                    fetchedLinks.add(item.link ?: "")
                    fetchedTitles.add(item.title ?: "")
                    fetchedImages.add(item.image ?: "")
                    fetchedDates.add(item.pubDate ?: "")
                    Log.d("TAG", "getData: ${item.sourceName}")
                    item.link?.let {
                        item.title?.let { it1 ->
                            item.image?.let { it2 ->
                                item.pubDate?.let { it3 ->

                                        feeds(
                                            id=0,
                                            feedlink = it,
                                            feedtitle = it1,
                                            imageurl = it2,
                                            data = it3,
                                            opened = "false",
                                            website = url,

                                        )

                                }
                            }
                        }
                    }?.let {
                        insert(
                            it
                        )
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            }

        rssData.postValue(
            RssData(
            links = fetchedLinks,
            titles = fetchedTitles,
            images = fetchedImages,
            dates = fetchedDates,


        )
        )




        _isloading.value=false
    }
    fun getwebsiteurlfromdb(){


        _urlsloaded.value=false

        _isloading.value=true

        var websites: List<String>
        try {

            viewModelScope.launch{
                withContext(Dispatchers.IO) {
                    var fetchedUrls=
                        supabaseclient.client.from("website").select()
                            .decodeList<website_supabase>()


                    fetchedUrls.forEach {
                        websiteinsert(websites(0,it.websitelink))
                    }
                    getData(getWebsiteLinks())

                    fetchedUrls= emptyList()


                }


            }

        }

        catch (e:Exception){
            Log.d("TAG", "getwebsiteurlfromdb: ${e.message}")
        }




        _isloading.value=false

    }
    fun updatedatabase(){
        var websites: List<String>
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val fetchedUrls=
                        supabaseclient.client.from("website").select()
                            .decodeList<website_supabase>()


                    websites=fetchedUrls.map { it.websitelink }

                    _websiteUrls.postValue(websites)
                }
            }
        }
        catch (e:Exception){
            Log.d("TAG", "getwebsiteurlfromdb: ${e.message}")
        }

        _websiteUrls.value?.forEach {
            websiteinsert(
                websites(
                id=0,
                websitelink = it
            )
            )
        }


    }
    fun getbookmarksdata(){


        var bookmarksdat: List<bookmarkdatabase>


            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    try{
                    bookmarksdat= supabaseclient.client.from("bookmarks").select().decodeList<bookmarkdatabase>()
                        _bookmarkdata.postValue(bookmarksdat)
                    }
                    catch (e:Exception){
                        Log.d("TAG", "getbookmarksdata: $e")

                    }


                }
            }




    }
    fun getupdatedata(){
        var updatedata: List<auto_update>
        try{
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    updatedata=supabaseclient.client.from("auto_update").select().decodeList<auto_update>()
                    //Log.d("TAG", "getupdatedata: ${supabaseclient.client.from("auto_update").select().decodeList<auto_update>()}")
                }
                //Log.d("TAG", "getupdatedata: $updatedata")
                _updatedata.postValue(updatedata)
            }
        }
        catch (e:Exception){
            Log.d("TAG", "getupdatedata: $e")
        }

    }






}
