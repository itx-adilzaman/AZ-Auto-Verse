package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CarEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AutoVerseDatabase : RoomDatabase() {

    abstract fun carDetailDao(): CarDetailDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AutoVerseDatabase? = null

        fun getInstance(context: Context): AutoVerseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoVerseDatabase::class.java,
                    "autoverse_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
