package com.example.rss_parser.database.feeddatabase.websitedatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface websitedao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertwebsite(websites: websites)

    @Query("DELETE FROM websites WHERE websitelink=:websitelink")
    suspend fun delete(websitelink: String)
    @Query("SELECT * FROM  websites ORDER BY ID ASC")
    fun getdata(): LiveData<List<websites>>

    @Query("DELETE FROM websites")
    suspend fun clear()

}