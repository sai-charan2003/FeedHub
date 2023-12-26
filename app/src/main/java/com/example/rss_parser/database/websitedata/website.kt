package com.example.rss_parser.database.websitedata

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(tableName = "websitesdata",indices = [Index(value = ["websitelink"], unique = true)])

data class website (
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val websitelink:String
)