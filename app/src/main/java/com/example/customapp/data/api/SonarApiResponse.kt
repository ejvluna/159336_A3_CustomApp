// data/api/SonarApiResponse.kt
package com.example.customapp.data.api

import com.google.gson.annotations.SerializedName

// Data class to represent the response from the Sonar API
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
    // Subdata class to represent a choice from the API response (i.e. the response from the API)
    data class Choice(
        @SerializedName("message")
        val message: Message,
        @SerializedName("index")
        val index: Int
    ) {
        data class Message(
            @SerializedName("role")
            val role: String,
            @SerializedName("content")
            val content: String
        )
    }
    // Subdata class to represent the usage from the API response (i.e. the number of tokens used)
    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int
    )
}
