package com.example.rss_parser.database.websitedata

import androidx.lifecycle.LiveData

class websiterepo(private val websitedao: websitedao) {

    val allwebsitelinks:LiveData<List<website>> = websitedao.getdata()

    suspend fun insert(website: website){
        websitedao.insert(website)

    }
    suspend fun delete(link: String){
        websitedao.delete(link)
    }


}