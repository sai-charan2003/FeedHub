package com.example.rss_parser.database.websitedata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [website::class], version = 1)
abstract class websiteDatabase : RoomDatabase() {
    abstract fun websiteDao(): websitedao


    companion object {
        @Volatile
        private var INSTANCE: websiteDatabase? = null

        fun getDatabase(context: Context): websiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    websiteDatabase::class.java,
                    "website_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}