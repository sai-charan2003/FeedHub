package com.example.rss_parser.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rss_parser.supabase.database.bookmarkdatabase


import com.example.rss_parser.rssdata.RssData
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.supabase.database.website_supabase
import com.prof18.rssparser.RssParserBuilder
import com.prof18.rssparser.model.RssChannel
import io.github.jan.supabase.postgrest.from

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.IOException

class viewmodel(context: Context):ViewModel() {

    private var _isloading= MutableStateFlow(true)
    var isLoading=_isloading.asStateFlow()
    var websiterb = mutableListOf<website_supabase>()
    private val _websiteUrls = MutableLiveData<List<String>>()
    val websiteUrls: LiveData<List<String>> = _websiteUrls
    private val _bookmarkdata = MutableLiveData<List<bookmarkdatabase>>()
    val bookmarkdata: LiveData<List<bookmarkdatabase>> = _bookmarkdata

    private val builder = RssParserBuilder(
        callFactory = OkHttpClient(),
        charset = Charsets.UTF_8,
    )

    private val rssParser = builder.build()

    val rssData = MutableLiveData<RssData>()
    fun setLoading(isLoading: Boolean) {
        _isloading.value = isLoading
    }

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

                rssFeed.items.forEach { item ->
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
    fun getwebsiteurlfromdb(){
        _isloading.value=true

        var websites: List<String>
        try {

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val fetchedUrls=
                        supabaseclient.client.from("website").select()
                            .decodeList<website_supabase>()
                    Log.d("TAG", "getwebsiteurlfromdb: $fetchedUrls")
                    websites=fetchedUrls.map { it.websitelink }
                    _websiteUrls.postValue(websites )

                }


            }
        }
        catch (e:Exception){
            Log.d("TAG", "getwebsiteurlfromdb: ${e.message}")
        }
        _isloading.value=false

    }
    fun getbookmarksdata(){


        var bookmarksdat: List<bookmarkdatabase>

        try{
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    bookmarksdat= supabaseclient.client.from("bookmarks").select().decodeList<bookmarkdatabase>()
                    }
                    _bookmarkdata.postValue(bookmarksdat)

                }
            }
        catch (e:Exception){
            Log.d("TAG", "getbookmarksdata: $e")

        }



    }






}
