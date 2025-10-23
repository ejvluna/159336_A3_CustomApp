// data/model/VerificationResult.kt

/**
 * Data models for verification results and related components.
 *
 * This file contains:
 * - VerificationResult: The main data class representing a fact-check result
 * - Rating: An enum defining possible verification statuses
 * - Data classes for API request/response models
 *
 * These models are used throughout the app to maintain consistent data handling
 * between the UI, database, and API layers.
 */

package com.example.customapp.data.model

// Data class that encapsulates all information about a verified claim
data class VerificationResult(
    val id: Int = 0,
    val claim: String,
    val rating: Rating,
    val summary: String,
    val explanation: String,
    val citations: List<Citation>,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Data class representing a citation with title, URL, and optional publication date
    // Updated May 2025: Perplexity API now returns search_results with detailed source information
    data class Citation(
        val title: String,
        val url: String,
        val date: String? = null
    )

    // Enum class to represent the available ratings for a verification result
    enum class Rating {
        TRUE,
        FALSE,
        MISLEADING,
        UNABLE_TO_VERIFY
    }
}
