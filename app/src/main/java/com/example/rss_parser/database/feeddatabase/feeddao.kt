package com.example.rss_parser.database.feeddatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@Dao
interface feeddao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(feeds: feeds)

    @Delete
    suspend fun delete(feeds: feeds)
    @Query("SELECT * FROM  feeds ORDER BY ID ASC")
    fun getdata(): LiveData<List<feeds>>
    @Update
    suspend fun update(feeds: feeds)
    @Query("DELETE FROM feeds")
    suspend fun clear()
    @Query("DELETE FROM feeds WHERE website = :website")
    suspend fun deleteFeedsByWebsite(website: String)

    @Query("""SELECT * FROM feeds JOIN feeds_fts ON feeds_fts.feedtitle== feeds.feedtitle WHERE feeds_fts.feedtitle MATCH :query""")
    fun searchbytitle(query: String): Flow<List<feeds>>




}