package com.example.customapp.data.api

import com.google.gson.annotations.SerializedName

data class SonarApiRequest(
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("model")
    val model: String,
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    @SerializedName("search_domain_filter")
    val searchDomainFilter: List<String>
) {
    data class Message(
        @SerializedName("role")
        val role: String,
        @SerializedName("content")
        val content: String
    )
}
