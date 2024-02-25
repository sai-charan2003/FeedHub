package com.example.rss_parser.database.feeddatabase.websitedatabase

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "websites",indices = [Index(value = ["websitelink"], unique = true)])

data class websites (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val websitelink:String

)