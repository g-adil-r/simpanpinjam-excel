package com.example.proyeksp.database

import android.content.Context
import androidx.room.Database
// import androidx.room.DatabaseConfiguration // Not needed for basic setup
// import androidx.room.InvalidationTracker // Not needed for basic setup
import androidx.room.Room // Import Room directly
import androidx.room.RoomDatabase
// import androidx.sqlite.db.SupportSQLiteOpenHelper // Not needed for basic setup
// import kotlin.concurrent.Volatile // @Volatile is part of Kotlin stdlib

@Database(entities = [Rekening::class], version = 1, exportSchema = false) // Added exportSchema = false (good practice)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rekeningDao(): RekeningDAO // << CHANGED: Must return non-null DAO

    // You don't need to override clearAllTables(), createInvalidationTracker(), createOpenHelper()
    // unless you have very specific custom logic. Room provides defaults.
    // override fun clearAllTables() {} // Remove if empty

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase { // << CHANGED: Return non-null AppDatabase
            return INSTANCE ?: synchronized(this) { // 'this' refers to Companion object scope
                // Double-check idiom for thread safety
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simpan_pinjam_db" // Consistent with your Java version
                )
                    .fallbackToDestructiveMigration() // Be careful with this in production
                    .build()
                INSTANCE = instance
                instance // Return the built instance
            }
        }
    }
}

//@Database(entities = [Rekening::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun rekeningDao(): RekeningDAO?
//
//    override fun clearAllTables() {
//    }
//
////    override fun createInvalidationTracker(): InvalidationTracker {
////        return null
////    }
////
////    override fun createOpenHelper(databaseConfiguration: DatabaseConfiguration): SupportSQLiteOpenHelper {
////        return null
////    }
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase? {
//            if (INSTANCE == null) {
//                synchronized(AppDatabase::class.java) {
//                    INSTANCE =
//                        databaseBuilder(
//                            context.applicationContext,
//                            AppDatabase::class.java, "simpan_pinjam_db"
//                        )
//                            .fallbackToDestructiveMigration()
//                            .build()
//                }
//            }
//            return INSTANCE
//        }
//    }
//}
