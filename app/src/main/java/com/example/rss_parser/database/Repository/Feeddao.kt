package com.example.rss_parser.database.Repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rss_parser.database.feeddatabase.feeds
import com.example.rss_parser.database.feeddatabase.websiteTileAndLink
import com.example.rss_parser.database.feeddatabase.websiteTitleAndFavicon
import kotlinx.coroutines.flow.Flow


@Dao
interface feedRepository{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(feeds: feeds)

    @Delete
    suspend fun delete(feeds: feeds)
    @Query("SELECT * FROM  feeds ORDER BY ID ASC")
    fun getdata(): LiveData<List<feeds>>
    @Update
    suspend fun update(feeds: feeds)
    @Query("UPDATE feeds SET opened =:opened WHERE id=:id")
    suspend fun UpdateOpened(id:Int, opened:String)
    @Query("DELETE FROM feeds")
    suspend fun clear()
    @Query("DELETE FROM feeds WHERE website = :website")
    suspend fun deleteFeedsByWebsite(website: String)
    @Query("SELECT * FROM feeds WHERE id = :id")
    fun getfeedbyid(id:Int): feeds



    @Query("""SELECT * FROM feeds JOIN feeds_fts ON feeds_fts.feedtitle== feeds.feedtitle WHERE feeds_fts.feedtitle MATCH :query ORDER BY date DESC""")
    fun searchbytitle(query: String): Flow<List<feeds>>
    @Query("SELECT * FROM  feeds ORDER BY ID ASC")
    fun getnonlivedata(): List<feeds>

    @Query("DELETE FROM feeds WHERE id IN (:id)")
    fun DeleteListOfData(id:List<Int>)
    @Query("SELECT DISTINCT websiteTitle,websiteFavicon from feeds")
    fun SelectDistinctWebsiteNames():Flow<List<websiteTitleAndFavicon?>>

    @Query("SELECT DISTINCT websiteTitle,website,websiteFavicon from feeds")
    fun selectDistinctWebsiteData():Flow<List<websiteTileAndLink>>
    @Query("SELECT DISTINCT website from feeds")
    fun selectDistinctWebsite():Flow<List<String?>>
    @Query("SELECT DISTINCT website from feeds")
    fun selectDistinctWebsiteWithOutFlow():List<String?>

    @Query("UPDATE feeds SET isWebsiteFav=:isWebsiteFav WHERE websiteTitle=:websiteTitle")
    fun updateIsWebsiteFav(isWebsiteFav:Boolean,websiteTitle:String)




}