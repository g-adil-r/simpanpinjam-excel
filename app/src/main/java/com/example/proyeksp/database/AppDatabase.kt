package com.example.proyeksp.database

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import kotlin.concurrent.Volatile

@Database(entities = [Rekening::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rekeningDao(): RekeningDAO?

    override fun clearAllTables() {
    }

//    override fun createInvalidationTracker(): InvalidationTracker {
//        return null
//    }
//
//    override fun createOpenHelper(databaseConfiguration: DatabaseConfiguration): SupportSQLiteOpenHelper {
//        return null
//    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE =
                        databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, "simpan_pinjam_db"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }
    }
}
