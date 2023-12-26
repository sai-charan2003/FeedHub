package com.example.rss_parser.database.bookmarkdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao

interface bookmarksdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmarks: bookmarks)
    @Query("DELETE FROM bookmarks WHERE websitelink=:link")
    suspend fun delete(link:String)
    @Query("SELECT * FROM  bookmarks ORDER BY ID ASC")
    fun getbookmarks(): LiveData<List<bookmarks>>

}