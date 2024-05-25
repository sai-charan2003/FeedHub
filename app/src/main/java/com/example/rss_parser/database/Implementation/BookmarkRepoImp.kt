package com.example.rss_parser.database.Implementation

import com.example.rss_parser.database.Repository.BookmarkRepo
import com.example.rss_parser.database.feeddatabase.Bookmarks
import kotlinx.coroutines.flow.Flow

class BookmarkRepoImp(private val bookmarkRepo:BookmarkRepo) {
    val allBookmarks: Flow<List<Bookmarks>> = bookmarkRepo.getData()


    suspend fun insert(bookmarks: Bookmarks){
        bookmarkRepo.insert(bookmarks)
    }

    suspend fun clearBookmarks(){
        bookmarkRepo.clearBookmarks()
    }
    suspend fun deleteBookmark(feedlink: String){
        bookmarkRepo.delete(feedlink)
    }
}