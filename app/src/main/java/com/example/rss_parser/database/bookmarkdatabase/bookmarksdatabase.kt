package com.example.rss_parser.database.bookmarkdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [bookmarks::class], version = 1)
abstract class bookmarksdatabase : RoomDatabase() {
    abstract fun bookmarks(): bookmarksdao


    companion object {
        @Volatile
        private var INSTANCE: bookmarksdatabase? = null

        fun getbookmarkDatabase(context: Context): bookmarksdatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    bookmarksdatabase::class.java,
                    "bookmarks_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}