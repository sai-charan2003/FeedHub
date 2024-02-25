package com.example.rss_parser.database.feeddatabase.websitedatabase

import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData

class websiterepo(private val websitedao: websitedao){
    val allwebsites:LiveData<List<websites>> = websitedao.getdata()

    suspend fun insert(websites: websites){
        websitedao.insertwebsite(websites)
    }
    suspend fun delete(websitelink: String){
        websitedao.delete(websitelink)
    }
    suspend fun clear(){
        websitedao.clear()
    }
}