// data/database/ClaimHistoryDao.kt

/**
 * Data Access Object (DAO) for verification history operations.
 *
 * This interface defines all database operations for the claim history feature:
 * - Querying all verification results
 * - Inserting new verification results
 * - Deleting verification results by ID
 * - Reactive data observation with Flow
 *
 * All database operations are executed on background threads automatically by Room.
 */

package com.example.customapp.data.database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO interface for managing claim history records in the local Room database
@Dao
interface ClaimHistoryDao {
    // Function that inserts a new claim record and return its auto-generated ID
    @Insert
    suspend fun insertClaim(claim: ClaimHistoryEntity): Long
    // Function that retrieves all claims sorted by most recent first; returns a Flow for real-time updates
    @Query("SELECT * FROM claim_history ORDER BY timestamp DESC")
    fun getAllClaims(): Flow<List<ClaimHistoryEntity>>
    // Function that retrieves a single claim by its ID; returns null if not found
    @Query("SELECT * FROM claim_history WHERE id = :id")
    suspend fun getClaimById(id: Int): ClaimHistoryEntity?
    // Function that deletes a claim by its ID
    @Query("DELETE FROM claim_history WHERE id = :id")
    suspend fun deleteClaimById(id: Int)
    // Function that clears all existing claims from the database
    @Query("DELETE FROM claim_history")
    suspend fun deleteAllClaims()
}
