package com.example.rss_parser.database.feeddatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rss_parser.database.Repository.BookmarkRepo
import com.example.rss_parser.database.Repository.feedRepository


@Database(entities = [feeds::class,feeds_fts::class,Bookmarks::class], version = 26)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedRepository(): feedRepository

    abstract fun BookmarkRepo(): BookmarkRepo

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase"
                )
                    .addMigrations(
                        MIGRATION_9_10,
                        MIGRATION_21_22,
                        MIGRATION_22_23,
                        MIGRATION_24_25)

                    .fallbackToDestructiveMigrationFrom(1).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
val MIGRATION_9_10= object : Migration(9,10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE feeds ADD COLUMN description TEXT DEFAULT NULL")
    }


}
val MIGRATION_21_22=object: Migration(21,22){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE feeds ADD COLUMN websiteTitle TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE feeds ADD COLUMN websiteFavicon TEXT DEFAULT NULL")
    }

}
val MIGRATION_22_23=object : Migration(22,21){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE bookmarks ADD COLUMN websiteTitle TEXT DEFAULT NULL")
        db.execSQL("ALTER TABLE bookmarks ADD COLUMN websiteFavicon TEXT DEFAULT NULL")
    }
}
val MIGRATION_24_25=object  : Migration(25,26){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE feeds ADD COLUMN isWebsiteFav INT DEFAULT NULL")
    }
}