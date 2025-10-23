// data/database/AppDatabase.kt

/**
 * Room database configuration for the Verifica app.
 *
 * This file contains:
 * - AppDatabase: The main database class with version and entity definitions
 * - Database access objects (DAOs) for each entity
 * - Database migration strategies
 * - Singleton pattern implementation for database access
 *
 * The database is used to store and retrieve verification history locally.
 */

package com.example.customapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Abstract class that extends RoomDatabase to define the database schema.
// RESOURCE MANAGEMENT: Singleton pattern with thread-safe initialization ensures single database instance which is kept in memory for app lifetime to avoid memory waste
@Database(entities = [ClaimHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // Abstract function that returns the DAO (Data Access Object) for database operations. Subclasses must implement this for CRUD operations.
    abstract fun claimHistoryDao(): ClaimHistoryDao
    
    // Companion object creates a static context where we can define the Singleton pattern without needing to instantiate AppDatabase
    companion object {
        // @Volatile ensures that changes to INSTANCE are immediately visible to all threads, preventing multiple database instances
        @Volatile
        // Create and store an instance of AppDatabase for the Singleton pattern
        private var INSTANCE: AppDatabase? = null

        // Factory method that create a single AppDatabase instance using lazy initialization (created only when first called, not at app startup)
        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance of the AppDatabase if it exists, otherwise call the builder method to create a new instance
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "claim_history_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
