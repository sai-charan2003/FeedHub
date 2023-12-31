package com.example.rss_parser.supabase.client

import com.example.rss_parser.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object supabaseclient {
    val client= createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY

    ){
        install(Auth){
            host="sai-charan2003.github.io"
            scheme="https"

        }
        install(Storage)
        install(Postgrest)
        install(Realtime)
    }
}