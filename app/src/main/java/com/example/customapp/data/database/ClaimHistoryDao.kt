package com.example.customapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClaimHistoryDao {
    @Insert
    suspend fun insertClaim(claim: ClaimHistoryEntity): Long

    @Query("SELECT * FROM claim_history ORDER BY timestamp DESC")
    fun getAllClaims(): Flow<List<ClaimHistoryEntity>>

    @Query("SELECT * FROM claim_history WHERE id = :id")
    suspend fun getClaimById(id: Int): ClaimHistoryEntity?

    @Query("DELETE FROM claim_history WHERE id = :id")
    suspend fun deleteClaimById(id: Int)

    @Delete
    suspend fun deleteClaim(claim: ClaimHistoryEntity)

    @Query("DELETE FROM claim_history")
    suspend fun deleteAllClaims()
}
