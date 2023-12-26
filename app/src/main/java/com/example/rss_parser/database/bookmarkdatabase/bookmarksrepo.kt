package com.example.rss_parser.database.bookmarkdatabase

import androidx.lifecycle.LiveData

class bookmarksrepo(private val bookmarksdao: bookmarksdao) {
    val allbookmarks:LiveData<List<bookmarks>> = bookmarksdao.getbookmarks()


    suspend fun insert(bookmarks: bookmarks){
        bookmarksdao.insert(bookmarks)
    }
    suspend fun delete(link:String){
        bookmarksdao.delete(link)
    }

}