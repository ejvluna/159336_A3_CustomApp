package com.example.customapp.data.model

data class VerificationResult(
    val claim: String,
    val rating: Rating,
    val summary: String,
    val explanation: String,
    val citations: List<String>
) {
    enum class Rating {
        MOSTLY_TRUE,
        MIXED,
        MOSTLY_FALSE
    }
}
