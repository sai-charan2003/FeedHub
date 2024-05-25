package com.example.rss_parser.database.feeddatabase

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

//@Entity(tableName = "websites",indices = [Index(value = ["websiteLink"], unique = true)])
//
//
//
//data class websites(
//    @PrimaryKey(autoGenerate = true)
//    val id:Int,
//    val websiteLink:String,
//    val email:String?=null
//)
@Serializable
data class supabaseWebsite(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    val websiteLink:String,
    val email:String?=null
)

