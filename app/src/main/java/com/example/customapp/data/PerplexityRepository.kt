// /data/PerplexityRepository.kt
/**
 * Central data access layer that coordinates between:
 * 1. Perplexity Sonar API for fact-checking queries
 * 2. Local Room database for storing verification history
 *
 * Handles data transformation, error handling, and provides a clean API for ViewModels.
 * This class is stateless - all operations are scoped to the calling coroutine.
 */

package com.example.customapp.data

// Import required packages to perform API calls and database operations
import android.util.Log
import com.example.customapp.config.ApiConfig
import com.example.customapp.data.api.SonarApiRequest
import com.example.customapp.data.api.SonarApiService
import com.example.customapp.data.database.ClaimHistoryDao
import com.example.customapp.data.database.ClaimHistoryEntity
import com.example.customapp.data.model.VerificationResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

// Class to encapsulate data operations for Perplexity API and local database
class PerplexityRepository(
    private val apiService: SonarApiService,
    private val claimHistoryDao: ClaimHistoryDao
) {
    // Create and store an instance of Gson to parse JSON responses
    private val gson = Gson()

    // Class Function to verify if the provided query is factually correct
    suspend fun verifyQuery(query: String): VerificationResult {
        val tag = "PerplexityRepository"
        return try {
            Log.d(tag, "Verifying query: $query")
            // Build JSON Schema for structured response per Sonar API requirements (Citations are returned separately in the API response, not in the JSON content)
            val jsonSchema = mapOf(
                "type" to "object",
                "properties" to mapOf(
                    "rating" to mapOf("type" to "string", "enum" to listOf("TRUE", "FALSE", "MISLEADING", "UNABLE_TO_VERIFY")),
                    "summary" to mapOf("type" to "string"),
                    "explanation" to mapOf("type" to "string")
                ),
                "required" to listOf("rating", "summary", "explanation")
            )
            // Store the response format from the JSON Schema
            val responseFormat = SonarApiRequest.ResponseFormat(
                jsonSchema = SonarApiRequest.JsonSchema(schema = jsonSchema)
            )
            // Build the request to the Sonar API: includes the query, model, temperature, max tokens, search domain filter, response format, and efficiency parameters
            val request = SonarApiRequest(
                messages = listOf(
                    SonarApiRequest.Message(
                        role = "user",
                        content = """ Analyze the claim using trusted sources and determine its factual rating using the categories defined below.

RATING DEFINITIONS:
- TRUE: Fully supported by credible evidence.
- FALSE: Directly contradicted by credible evidence.
- MISLEADING: Contains partial truths but omits essential context or is presented in a deceptive way.
- UNABLE_TO_VERIFY: Insufficient or inconclusive evidence is available.

RESPONSE REQUIREMENTS:
- Provide a concise summary and detailed explanation with citations from trusted sources.
- For TRUE, FALSE, and MISLEADING ratings, you MUST provide at least 2 credible citations from trusted sources to support your rating. For UNABLE_TO_VERIFY, this is not required as it may not apply.
- Use clear, neutral, and concise language suitable for general readers, so the reader understands both result and reasoning.
Claim: $query"""
                    )
                ),
                model = ApiConfig.MODEL_SONAR,
                temperature = ApiConfig.DEFAULT_TEMPERATURE,
                maxTokens = ApiConfig.DEFAULT_MAX_TOKENS,
                searchDomainFilter = ApiConfig.DEFAULT_SEARCH_DOMAIN_FILTER,
                responseFormat = responseFormat,
                maxTokensPerPage = ApiConfig.DEFAULT_MAX_TOKENS_PER_PAGE,
                maxResults = ApiConfig.DEFAULT_MAX_RESULTS,
                numSources = ApiConfig.DEFAULT_NUM_SOURCES
            )
            // Set the API key in the header for authentication
            val authHeader = "Bearer ${ApiConfig.API_KEY}"
            // Make the API call to the Sonar API and store the response
            val response = apiService.verifyClaim(authHeader, request)
            // Extract the content from the API response (choices)
            val content = response.choices.firstOrNull()?.message?.content ?: ""
            // When there is no content in the response, return an error and notify the user
            if (content.isBlank()) {
                Log.w(tag, "API returned empty response")
                return VerificationResult(
                    claim = query,
                    rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                    summary = "No response from API",
                    explanation = "The API returned an empty response. Please try again.",
                    citations = emptyList()
                )
            }
            // Otherwise log the success and parse the API response
            Log.d(tag, "Query verified successfully")
            // Extract citations from search_results (May 2025 API update: citations field deprecated, now using search_results)
            Log.d(tag, "Raw API response: ${gson.toJson(response)}")
            Log.d(tag, "Search results count: ${response.searchResults?.size ?: 0}")
            response.searchResults?.forEach { result ->
                Log.d(tag, "  Citation: ${result.title} - ${result.url} (${result.date})")
            }
            val apiCitations = response.searchResults?.map { 
                VerificationResult.Citation(title = it.title, url = it.url, date = it.date)
            } ?: emptyList()
            parseApiResponse(query, content, apiCitations)
            // Handle HTTP exceptions by logging the error and returning an error and appropriate message, and set the result to UNABLE_TO_VERIFY
        } catch (e: HttpException) {
            Log.e(tag, "HTTP Error: ${e.code()} - ${e.message()}")
            val errorMessage = when (e.code()) {
                401 -> "Invalid API Key: Authentication failed. Please check your API key configuration."
                429 -> "Rate Limited: Too many requests. Please wait a moment and try again."
                500, 502, 503 -> "Server Error: The API service is temporarily unavailable. Please try again later."
                else -> "HTTP Error ${e.code()}: ${e.message()}"
            }
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "API Error",
                explanation = errorMessage,
                citations = emptyList()
            )

            // Handle socket timeout exceptions by logging the error and returning an error and appropriate message, and set the result to UNABLE_TO_VERIFY
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(tag, "Network Timeout: ${e.message}")
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "Network Timeout",
                explanation = "The request took too long. Please check your internet connection and try again.",
                citations = emptyList()
            )

            // Handle exceptions by logging the error and returning an error and appropriate message, and set the result to UNABLE_TO_VERIFY
        } catch (e: java.net.ConnectException) {
            Log.e(tag, "Connection Error: ${e.message}")
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "Connection Error",
                explanation = "Failed to connect to the API. Please check your internet connection.",
                citations = emptyList()
            )
        } catch (e: com.google.gson.JsonSyntaxException) {
            Log.e(tag, "Malformed Response: ${e.message}")
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "Invalid Response Format",
                explanation = "The API returned an unexpected response format. Please try again.",
                citations = emptyList()
            )
        } catch (e: Exception) {
            Log.e(tag, "Unexpected Error: ${e::class.simpleName} - ${e.message}", e)
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "Verification Failed",
                explanation = "An unexpected error occurred: ${e.message ?: "Unknown error"}",
                citations = emptyList()
            )
        }
    }

    // Class Function to get the history of queries from the database
    fun getHistory(): Flow<List<VerificationResult>> {
        // Retrieve all claims from the database and convert them to VerificationResult objects
        return claimHistoryDao.getAllClaims().map { entities ->
            entities.map { entity ->
                VerificationResult(
                    id = entity.id,
                    claim = entity.query,
                    rating = VerificationResult.Rating.valueOf(entity.result),
                    summary = entity.summary,
                    explanation = entity.explanation,
                    citations = parseCitations(entity.citations),
                    timestamp = entity.timestamp
                )
            }
        }
    }

    // Class Function to save a query to the database
    suspend fun saveQuery(result: VerificationResult) {
        val entity = ClaimHistoryEntity(
            query = result.claim,
            result = result.rating.name,
            summary = result.summary,
            explanation = result.explanation,
            citations = gson.toJson(result.citations),
            timestamp = System.currentTimeMillis()
        )
        claimHistoryDao.insertClaim(entity)
    }

    // Class Function to delete a query from the database
    suspend fun deleteQuery(id: Int) {
        claimHistoryDao.deleteClaimById(id)
    }

    // Class Function to parse the API response and extract rating, summary, and explanation
    private fun parseApiResponse(claim: String, content: String, apiCitations: List<VerificationResult.Citation>): VerificationResult {
        val tag = "PerplexityRepository"
        // Parse JSON response (without citations - those come from API response)
        val jsonObject = gson.fromJson(content, com.google.gson.JsonObject::class.java)
        // Store the rating from the API response (default to UNABLE_TO_VERIFY if not found)
        val ratingStr = jsonObject.get("rating")?.asString ?: "UNABLE_TO_VERIFY"
        // Set the rating based on the rating string
        val rating = when (ratingStr.uppercase()) {
            "TRUE", "MOSTLY_TRUE" -> VerificationResult.Rating.TRUE
            "FALSE", "MOSTLY_FALSE" -> VerificationResult.Rating.FALSE
            "MISLEADING" -> VerificationResult.Rating.MISLEADING
            "UNABLE_TO_VERIFY" -> VerificationResult.Rating.UNABLE_TO_VERIFY
            else -> VerificationResult.Rating.UNABLE_TO_VERIFY
        }
        // Store the summary and explanation from the API response
        val summary = jsonObject.get("summary")?.asString ?: ""
        val explanation = jsonObject.get("explanation")?.asString ?: ""
        // Log the parsed response for debugging
        Log.d(tag, "Parsed JSON response: rating=$ratingStr, citations=${apiCitations.size}")
        apiCitations.forEach { Log.d(tag, "  Citation: ${it.title} - ${it.url}") }
        // Return the parsed response
        return VerificationResult(
            claim = claim,
            rating = rating,
            summary = summary,
            explanation = explanation,
            citations = apiCitations
        )
    }

    // Class Function to parse the citations from the database and convert them to Citation objects
    // Handles both old format (List<String>) and new format (List<Citation>) for backward compatibility
    private fun parseCitations(citationsJson: String): List<VerificationResult.Citation> {
        return try {
            // Try to parse as Citation objects first (new format)
            val citationArray = gson.fromJson(citationsJson, Array<VerificationResult.Citation>::class.java)
            citationArray.toList()
        } catch (e: Exception) {
            // Fallback: try to parse as strings (old format) and convert to Citation objects
            try {
                val urlArray = gson.fromJson(citationsJson, Array<String>::class.java)
                urlArray.map { url ->
                    VerificationResult.Citation(title = url, url = url, date = null)
                }
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

}
