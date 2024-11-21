package com.example.storyapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.utils.RemoteKeys


@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 3,
    exportSchema = false 
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                
                database.execSQL("ALTER TABLE story ADD COLUMN author TEXT DEFAULT NULL")
            }
        }

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "story_database"
                )
                    .addMigrations(MIGRATION_2_3) 
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}