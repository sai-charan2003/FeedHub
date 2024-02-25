package com.example.rss_parser.database.feeddatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [feeds::class,feeds_fts::class], version = 9)
abstract class feeddatabase : RoomDatabase() {
    abstract fun feeddao(): feeddao


    companion object {
        @Volatile
        private var INSTANCE: feeddatabase? = null

        fun getDatabase(context: Context): feeddatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    feeddatabase::class.java,
                    "feed_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}