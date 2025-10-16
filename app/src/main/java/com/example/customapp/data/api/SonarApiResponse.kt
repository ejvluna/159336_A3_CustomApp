package com.example.customapp.data.api

import com.google.gson.annotations.SerializedName

data class SonarApiResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("usage")
    val usage: Usage
) {
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

    data class Usage(
        @SerializedName("prompt_tokens")
        val promptTokens: Int,
        @SerializedName("completion_tokens")
        val completionTokens: Int
    )
}
