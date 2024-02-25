package com.example.rss_parser.database.feeddatabase

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class feedrepo(private val feeddao: feeddao) {
    val allfeeds:LiveData<List<feeds>> = feeddao.getdata()

    suspend fun insert(feeds: feeds) {
        feeddao.insert(feeds)


    }

    suspend fun update(feeds: feeds){
        feeddao.update(feeds)

    }
    suspend fun delete(feeds: feeds){
        feeddao.delete(feeds)
    }
    suspend fun clear(){
        feeddao.clear()
    }
    suspend fun deleteFeedsByWebsite(website: String) {
        feeddao.deleteFeedsByWebsite(website)
    }
    fun search(query:String) : Flow<List<feeds>> {
        return feeddao.searchbytitle(query)
    }
}