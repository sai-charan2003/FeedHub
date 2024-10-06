package dev.charan.feedhub.supabase.database

import kotlinx.serialization.Serializable

@Serializable

data class auto_update(
        val id:Int?=null,
        val download_link:String,
        val version_number:String,
        val new_features :String

        )