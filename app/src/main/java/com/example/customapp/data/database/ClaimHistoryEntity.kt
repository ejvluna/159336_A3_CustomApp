// data/database/ClaimHistoryEntity.kt
package com.example.customapp.data.database

// Import packages required for functionality
import androidx.room.Entity
import androidx.room.PrimaryKey

// Room entity to encapsulate claim history class
@Entity(tableName = "claim_history")
data class ClaimHistoryEntity(
    // Auto-incrementing primary key initialized with default value of 0
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // The user created query
    val query: String,
    // The result (rating) returned by SonarAPI: TRUE, FALSE, MISLEADING, UNABLE_TO_VERIFY
    val result: String,
    // The summary/explanation of the verification result from SonarAPI
    val summary: String,
    // The citations provided by SonarAPI for the result
    val citations: String,
    // The timestamp of the query for sorting and display purposes
    val timestamp: Long
)
