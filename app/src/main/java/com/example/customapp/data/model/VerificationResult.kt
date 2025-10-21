// data/model/VerificationResult.kt
package com.example.customapp.data.model

// Data class that encapsulates all information about a verified claim
data class VerificationResult(
    val id: Int = 0,
    val claim: String,
    val rating: Rating,
    val summary: String,
    val explanation: String,
    val citations: List<String>,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Enum class to represent the available ratings for a verification result
    enum class Rating {
        TRUE,
        FALSE,
        MISLEADING,
        UNABLE_TO_VERIFY
    }
}
