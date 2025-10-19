// data/database/AppDatabase.kt
package com.example.customapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database for claim history
@Database(entities = [ClaimHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // Data Access Object (DAO) for reading and writing a claim to the database
    abstract fun claimHistoryDao(): ClaimHistoryDao
    // Singleton instance of the database
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Factory method to get the database instance
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Create the database instance with Room database builder
                val instance = Room.databaseBuilder(
                    // Set the app as the context and build the database
                    context.applicationContext,
                    AppDatabase::class.java,
                    "claim_history_database"
                ).build()
                // Return the database instance
                INSTANCE = instance
                instance
            }
        }
    }
}
