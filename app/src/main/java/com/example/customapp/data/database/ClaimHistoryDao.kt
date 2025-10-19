// data/database/ClaimHistoryDao.kt
package com.example.customapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object (DAO) interface for claim history
@Dao
interface ClaimHistoryDao {
    // Function to insert a claim into the database
    @Insert
    suspend fun insertClaim(claim: ClaimHistoryEntity): Long
    // Function to get all claims from the database
    @Query("SELECT * FROM claim_history ORDER BY timestamp DESC")
    fun getAllClaims(): Flow<List<ClaimHistoryEntity>>
    // Function to get a claim by ID
    @Query("SELECT * FROM claim_history WHERE id = :id")
    suspend fun getClaimById(id: Int): ClaimHistoryEntity?
    // Function to delete a claim by ID
    @Query("DELETE FROM claim_history WHERE id = :id")
    suspend fun deleteClaimById(id: Int)
    // Function to delete a claim
    @Delete
    suspend fun deleteClaim(claim: ClaimHistoryEntity)
    // Function to delete all claims
    @Query("DELETE FROM claim_history")
    suspend fun deleteAllClaims()
}
