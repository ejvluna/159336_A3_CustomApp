// data/model/VerificationResult.kt
package com.example.customapp.data.model

// Data class to represent the result of a verification
data class VerificationResult(
    val claim: String,
    val rating: Rating,
    val summary: String,
    val explanation: String,
    val citations: List<String>
) {
    // Enum class to represent the available ratings for a verification result
    enum class Rating {
        MOSTLY_TRUE,
        MIXED,
        MOSTLY_FALSE
    }
}
