package com.example.rss_parser.database.websitedata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao

interface websitedao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(websitedata: website)
    @Query("DELETE FROM websitesdata WHERE websitelink=:link")
    suspend fun delete(link: String)
    @Query("SELECT * FROM  websitesdata ORDER BY ID ASC")
    fun getdata(): LiveData<List<website>>
}