package com.example.customapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

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
