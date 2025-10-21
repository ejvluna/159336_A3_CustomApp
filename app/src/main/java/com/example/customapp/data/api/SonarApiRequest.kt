// /data/api/SonarApiRequest.kt
package com.example.customapp.data.api

import com.google.gson.annotations.SerializedName

// Data class that maps to the JSON request body for the Perplexity Sonar API.
data class SonarApiRequest(
    // Conversation messages containing the user query and system instructions
    @SerializedName("messages")
    val messages: List<Message>,
    // The AI model to use for fact-checking (e.g., "sonar" or "sonar-pro")
    @SerializedName("model")
    val model: String,
    // Controls randomness in responses (0.0 = deterministic, higher = more creative)
    @SerializedName("temperature")
    val temperature: Float,
    // Maximum number of tokens to generate in the response
    @SerializedName("max_tokens")
    val maxTokens: Int,
    // List of trusted domains that can be used for fact-checking sources
    @SerializedName("search_domain_filter")
    val searchDomainFilter: List<String>,
    // Optional response format configuration; when provided, enforces JSON Schema structure for fact-check results
    @SerializedName("response_format")
    val responseFormat: ResponseFormat? = null,
    // Maximum tokens to extract from each webpage during search (512 = concise content)
    @SerializedName("max_tokens_per_page")
    val maxTokensPerPage: Int? = null,
    // Maximum number of search results to process for fact-checking
    @SerializedName("max_results")
    val maxResults: Int? = null,
    // Maximum number of citations to include in the response
    @SerializedName("num_sources")
    val numSources: Int? = null
) {
    // Nested data class representing a single message in the conversation (e.g., user query or system instruction)
    data class Message(
        @SerializedName("role")
        val role: String,
        @SerializedName("content")
        val content: String
    )

    // Nested data class that specifies the response format as JSON Schema, enabling structured fact-check results
    data class ResponseFormat(
        @SerializedName("type")
        val type: String = "json_schema",
        @SerializedName("json_schema")
        val jsonSchema: JsonSchema
    )

    // Nested data class that defines the JSON Schema structure for the API response, ensuring fact-check data is returned in the expected format
    data class JsonSchema(
        @SerializedName("name")
        val name: String = "FactCheckResult",
        @SerializedName("schema")
        val schema: Map<String, Any>
    )
}
