// data/api/SonarApiResponse.kt

/**
 * Data classes for parsing responses from the Perplexity Sonar API.
 *
 * This file contains:
 * - Response structure for fact-checking results
 * - Nested data classes for complex response objects
 * - Data classes for citations and usage information
 * - Null-safety for optional fields
 *
 * These classes are deserialized from JSON using Gson when receiving API responses.
 */

package com.example.customapp.data.api

import com.google.gson.annotations.SerializedName

// Data class that maps the JSON response from the Perplexity Sonar API. Contains the model's response choices, token usage, and citations.
data class SonarApiResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("usage")
    val usage: Usage,
    @SerializedName("citations")
    val citations: List<String>? = null
) {
    // Nested data class representing a single response choice from the model (the API can return multiple choices)
    data class Choice(
        @SerializedName("message")
        val message: Message,
        @SerializedName("index")
        val index: Int
    ) {
        // Nested data class containing the model's response message with role (e.g., "assistant") and the fact-check content
        data class Message(
            @SerializedName("role")
            val role: String,
            @SerializedName("content")
            val content: String
        )
    }
    // Nested data class tracking token consumption: promptTokens (input) and completionTokens (output) for API billing/monitoring
    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int
    )
}
