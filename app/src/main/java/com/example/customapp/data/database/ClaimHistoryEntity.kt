// data/database/ClaimHistoryEntity.kt
package com.example.customapp.data.database

// Import packages required for functionality
import androidx.room.Entity
import androidx.room.PrimaryKey

// Room entity to encapsulate claim history class
@Entity(tableName = "claim_history")
data class ClaimHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String,
    val result: String,
    val status: String,
    val citations: String,
    val timestamp: Long
)
