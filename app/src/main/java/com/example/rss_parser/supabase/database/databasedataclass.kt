package com.example.rss_parser.supabase.database

import kotlinx.serialization.Serializable

@Serializable

data class bookmarkdatabase (
    val id:Int?=null,
    val websitelink:String,
    var email:String?=null,
    var images:String,
    var title:String
)