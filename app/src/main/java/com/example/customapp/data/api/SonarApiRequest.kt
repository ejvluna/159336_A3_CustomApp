// /data/api/SonarApiRequest.kt
package com.example.customapp.data.api

// Import required packages to perform API calls and handle JSON data
import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject

// Class to represent the request to the Sonar API
data class SonarApiRequest(
    // Serialized variables for the different fields of the request to the Sonar API
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("model")
    val model: String,
    @SerializedName("temperature")
    val temperature: Float,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    @SerializedName("search_domain_filter")
    val searchDomainFilter: List<String>,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat? = null
) {
    // Inner class to represent a message in the request
    data class Message(
        @SerializedName("role")
        val role: String,
        @SerializedName("content")
        val content: String
    )

    // Inner class to represent the response format of the request
    data class ResponseFormat(
        @SerializedName("type")
        val type: String = "json_schema",
        @SerializedName("json_schema")
        val jsonSchema: JsonSchema
    )

    // Inner class to represent the JSON schema of the response to map the API response to a data class
    data class JsonSchema(
        @SerializedName("name")
        val name: String = "FactCheckResult",
        @SerializedName("schema")
        val schema: Map<String, Any>
    )
}
