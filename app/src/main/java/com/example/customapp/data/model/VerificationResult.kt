// data/model/VerificationResult.kt
package com.example.customapp.data.model

// Data class to encapsulate the various fields of a verification result (query result)
data class VerificationResult(
    val claim: String,
    val rating: Rating,
    val summary: String,
    val explanation: String,
    val citations: List<String>
) {
    // Enum class to represent the available ratings for a verification result
    enum class Rating {
        TRUE,
        FALSE,
        MISLEADING,
        UNABLE_TO_VERIFY
    }
}
