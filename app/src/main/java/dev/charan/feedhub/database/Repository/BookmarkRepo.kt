package dev.charan.feedhub.database.Repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.charan.feedhub.database.feeddatabase.Bookmarks
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkRepo {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookmarks: Bookmarks)

    @Query("DELETE FROM bookmarks WHERE websiteLink=:websitelink")
    suspend fun delete(websitelink:String)

    @Query("DELETE FROM bookmarks")
    suspend fun clearBookmarks()


    @Query("SELECT * FROM bookmarks ORDER BY ID DESC")
    fun getData(): Flow<List<Bookmarks>>


}