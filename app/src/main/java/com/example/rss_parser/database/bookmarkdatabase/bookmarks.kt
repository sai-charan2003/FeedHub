package com.example.rss_parser.database.bookmarkdatabase

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("bookmarks",indices = [Index(value = ["websitelink"], unique = true)])

data class bookmarks (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val websitelink:String,
    val title:String,
    val images:String
)
