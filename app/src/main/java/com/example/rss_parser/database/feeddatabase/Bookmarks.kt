package com.example.rss_parser.database.feeddatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "bookmarks")
@Serializable
data class Bookmarks(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    val websitelink:String?,
    var email:String?=null,
    var images:String?,
    var title:String?,
    var website:String?,
    var date:String?=null,
    val websiteTitle:String?,
    val websiteFavicon:String?
)