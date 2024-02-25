package com.example.rss_parser.database.feeddatabase.websitedatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [websites::class], version = 1)
abstract class websitedatabase : RoomDatabase() {
    abstract fun websitedao(): websitedao


    companion object {
        @Volatile
        private var INSTANCE: websitedatabase? = null

        fun getDatabase(context: Context): websitedatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    websitedatabase::class.java,
                    "website_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}