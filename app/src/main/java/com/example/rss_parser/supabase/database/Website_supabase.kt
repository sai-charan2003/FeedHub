package com.example.rss_parser.supabase.database

import kotlinx.serialization.Serializable

@Serializable

data class website_supabase (
    val id:Int?=null,

    val websitelink:String,
    val email:String?=null
)