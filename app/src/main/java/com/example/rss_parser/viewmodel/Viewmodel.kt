package com.example.rss_parser.viewmodel

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rss_parser.database.bookmarkdatabase.bookmarks
import com.example.rss_parser.database.bookmarkdatabase.bookmarksdatabase
import com.example.rss_parser.database.bookmarkdatabase.bookmarksrepo
import com.example.rss_parser.database.websitedata.website
import com.example.rss_parser.database.websitedata.websiteDatabase
import com.example.rss_parser.database.websitedata.websiterepo
import com.example.rss_parser.rssdata.RssData
import com.prof18.rssparser.RssParserBuilder
import com.prof18.rssparser.model.RssChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.IOException

class viewmodel(context: Context):ViewModel() {
    val websiterepo: websiterepo
    val allwebsitelinks:LiveData<List<website>>
    val bookmarksrepo:bookmarksrepo
    val allbookmarks:LiveData<List<bookmarks>>
    private var _isloading= MutableStateFlow(true)
    var isLoading=_isloading.asStateFlow()

    init {
        val dao= websiteDatabase.getDatabase(context).websiteDao()
        val bookmarksdao=bookmarksdatabase.getbookmarkDatabase(context).bookmarks()
        websiterepo= websiterepo(dao)
        allwebsitelinks=websiterepo.allwebsitelinks
        bookmarksrepo=bookmarksrepo(bookmarksdao)
        allbookmarks=bookmarksrepo.allbookmarks

    }
    fun insert(website: website)=viewModelScope.launch {
        websiterepo.insert(website)
    }
    fun delete(link:String)=viewModelScope.launch {
        websiterepo.delete(link)
    }
    fun insertbookmark(bookmarks: bookmarks)=viewModelScope.launch {
        bookmarksrepo.insert(bookmarks)
    }
    fun deletebookmark(links:String)=viewModelScope.launch {
        bookmarksrepo.delete(links)
    }

    val builder = RssParserBuilder(
        callFactory = OkHttpClient(),
        charset = Charsets.UTF_8,
    )

    val rssParser = builder.build()

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

                rssFeed?.items?.forEach { item ->
                    fetchedLinks.add(item.link ?: "")
                    fetchedTitles.add(item.title ?: "")
                    fetchedImages.add(item.image ?: "")
                    fetchedDates.add(item.pubDate ?: "")

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


}