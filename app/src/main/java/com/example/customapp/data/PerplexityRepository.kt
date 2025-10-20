// /data/PerplexityRepository.kt
package com.example.customapp.data

// Import required packages to perform API calls and database operations
import android.util.Log
import com.example.customapp.config.ApiConfig
import com.example.customapp.config.TrustedSources
import com.example.customapp.data.api.SonarApiRequest
import com.example.customapp.data.api.SonarApiService
import com.example.customapp.data.database.ClaimHistoryDao
import com.example.customapp.data.database.ClaimHistoryEntity
import com.example.customapp.data.model.VerificationResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

// Class to handle API calls and database operations: input parameters are SonarApiService and ClaimHistoryDao
class PerplexityRepository(
    private val apiService: SonarApiService,
    private val claimHistoryDao: ClaimHistoryDao
) {
    // Create and store an instance of Gson to parse JSON responses
    private val gson = Gson()

    // Function to verify a query
    suspend fun verifyQuery(query: String): VerificationResult {
        val tag = "PerplexityRepository"

        // Try to verify the query by making an API call
        return try {
            Log.d(tag, "Verifying query: $query")
            
            // Build JSON Schema for structured response per Sonar API requirements
            // Note: Citations are returned separately in the API response, not in the JSON content
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

            // Build the request to the Sonar API: includes the query, model, temperature, max tokens, search domain filter, response format
            val request = SonarApiRequest(
                messages = listOf(
                    SonarApiRequest.Message(
                        role = "user",
                        content = """Fact-check the following claim and provide a rating with these definitions:
- TRUE: The claim is fully supported by credible evidence
- FALSE: The claim is contradicted by credible evidence
- MISLEADING: The claim contains some truth but is presented deceptively or lacks important context
- UNABLE_TO_VERIFY: There is insufficient evidence to verify the claim

Provide a concise summary and detailed explanation with citations from trusted sources.

Claim: $query"""
                    )
                ),
                model = ApiConfig.MODEL_SONAR,
                temperature = ApiConfig.DEFAULT_TEMPERATURE,
                maxTokens = ApiConfig.DEFAULT_MAX_TOKENS,
                searchDomainFilter = ApiConfig.DEFAULT_SEARCH_DOMAIN_FILTER,
                responseFormat = responseFormat
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

            // Store the citations from the API response
            val apiCitations = response.citations ?: emptyList()
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

            // Handle connection exceptions by logging the error and returning an error and appropriate message, and set the result to UNABLE_TO_VERIFY
        } catch (e: java.net.ConnectException) {
            Log.e(tag, "Connection Error: ${e.message}")
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "Connection Error",
                explanation = "Failed to connect to the API. Please check your internet connection.",
                citations = emptyList()
            )

            // Handle JSON syntax exceptions by logging the error and returning an error and appropriate message, and set the result to UNABLE_TO_VERIFY
        } catch (e: com.google.gson.JsonSyntaxException) {
            Log.e(tag, "Malformed Response: ${e.message}")
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "Invalid Response Format",
                explanation = "The API returned an unexpected response format. Please try again.",
                citations = emptyList()
            )

            // Handle unexpected exceptions by logging the error and returning an error and appropriate message, and set the result to UNABLE_TO_VERIFY
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

    // Function to get the history of queries from the database
    fun getHistory(): Flow<List<VerificationResult>> {
        return claimHistoryDao.getAllClaims().map { entities ->
            entities.map { entity ->
                VerificationResult(
                    claim = entity.query,
                    rating = VerificationResult.Rating.valueOf(entity.result),
                    summary = entity.status,
                    explanation = entity.citations,
                    citations = parseCitations(entity.citations)
                )
            }
        }
    }

    // Function to save a query to the database
    suspend fun saveQuery(result: VerificationResult) {
        val entity = ClaimHistoryEntity(
            query = result.claim,
            result = result.rating.name,
            status = result.summary,
            citations = gson.toJson(result.citations),
            timestamp = System.currentTimeMillis()
        )
        claimHistoryDao.insertClaim(entity)
    }

    // Function to delete a query from the database
    suspend fun deleteQuery(id: Int) {
        claimHistoryDao.deleteClaimById(id)
    }

    // Function to parse the API response
    private fun parseApiResponse(claim: String, content: String, apiCitations: List<String>): VerificationResult {
        val tag = "PerplexityRepository"
        
        // Parse JSON response (without citations - those come from API response)
        val jsonObject = gson.fromJson(content, com.google.gson.JsonObject::class.java)
        
        val ratingStr = jsonObject.get("rating")?.asString ?: "UNABLE_TO_VERIFY"
        val rating = when (ratingStr.uppercase()) {
            "TRUE" -> VerificationResult.Rating.TRUE
            "FALSE" -> VerificationResult.Rating.FALSE
            "MISLEADING" -> VerificationResult.Rating.MISLEADING
            "UNABLE_TO_VERIFY" -> VerificationResult.Rating.UNABLE_TO_VERIFY
            else -> VerificationResult.Rating.UNABLE_TO_VERIFY
        }
        
        val summary = jsonObject.get("summary")?.asString ?: ""
        val explanation = jsonObject.get("explanation")?.asString ?: ""
        
        Log.d(tag, "Parsed JSON response: rating=$ratingStr, citations=${apiCitations.size}")
        apiCitations.forEach { Log.d(tag, "  Citation: $it") }
        
        return VerificationResult(
            claim = claim,
            rating = rating,
            summary = summary,
            explanation = explanation,
            citations = apiCitations
        )
    }

    // Function to parse the citations from the API response
    private fun parseCitations(citationsJson: String): List<String> {
        return try {
            gson.fromJson(citationsJson, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Function to test the API connection
    suspend fun testApiConnection(): TestResult {
        val tag = "PerplexityRepository"
        return try {
            Log.d(tag, "=== Starting API Connection Test ===")
            
            Log.d(tag, "Step 1: Verifying API Key")
            val apiKey = ApiConfig.API_KEY
            if (apiKey.isBlank() || apiKey == "your_api_key_here") {
                Log.e(tag, "API Key is missing or not configured")
                return TestResult(
                    success = false,
                    message = "API Key is missing or not configured",
                    details = "Please set PERPLEXITY_API_KEY in secrets.properties"
                )
            }
            Log.d(tag, "✓ API Key found (length: ${apiKey.length})")
            
            Log.d(tag, "Step 2: Creating test request")
            val testQuery = "Is the Earth round?"
            val request = SonarApiRequest(
                messages = listOf(
                    SonarApiRequest.Message(
                        role = "user",
                        content = "Fact-check this claim briefly: $testQuery"
                    )
                ),
                model = ApiConfig.MODEL_SONAR,
                temperature = ApiConfig.DEFAULT_TEMPERATURE,
                maxTokens = ApiConfig.DEFAULT_MAX_TOKENS,
                searchDomainFilter = ApiConfig.DEFAULT_SEARCH_DOMAIN_FILTER
            )
            Log.d(tag, "✓ Request created: model=${request.model}, maxTokens=${request.maxTokens}")
            Log.d(tag, "✓ Search domain filter: ${request.searchDomainFilter.take(100)}...")
            
            Log.d(tag, "Step 3: Sending API request")
            val authHeader = "Bearer $apiKey"
            Log.d(tag, "✓ Auth header prepared (Bearer token)")
            
            val response = apiService.verifyClaim(authHeader, request)
            Log.d(tag, "✓ API response received")
            
            Log.d(tag, "Step 4: Parsing response")
            Log.d(tag, "Response ID: ${response.id}")
            Log.d(tag, "Choices count: ${response.choices.size}")
            Log.d(tag, "Usage - Prompt tokens: ${response.usage.promptTokens}, Completion tokens: ${response.usage.completionTokens}")
            
            val content = response.choices.firstOrNull()?.message?.content ?: ""
            if (content.isBlank()) {
                Log.w(tag, "Response content is empty")
                return TestResult(
                    success = false,
                    message = "API returned empty response",
                    details = "Response choices were empty or message content was blank"
                )
            }
            Log.d(tag, "✓ Response content length: ${content.length} characters")
            Log.d(tag, "Response preview: ${content.take(150)}...")
            
            val citations = response.citations ?: emptyList()
            Log.d(tag, "✓ Citations from API: ${citations.size} found")
            citations.forEach { Log.d(tag, "  - $it") }
            
            Log.d(tag, "=== API Connection Test SUCCESSFUL ===")
            TestResult(
                success = true,
                message = "API connection verified successfully",
                details = "Test query: '$testQuery'\nResponse length: ${content.length} chars\nCitations found: ${citations.size}\nTokens used: ${response.usage.promptTokens + response.usage.completionTokens}"
            )
        } catch (e: Exception) {
            Log.e(tag, "=== API Connection Test FAILED ===", e)
            Log.e(tag, "Error type: ${e::class.simpleName}")
            Log.e(tag, "Error message: ${e.message}")
            TestResult(
                success = false,
                message = "API connection test failed",
                details = "${e::class.simpleName}: ${e.message}"
            )
        }
    }

    // Function to test domain filtering is working (only allowed domains should be used)
    suspend fun testDomainFiltering(): TestResult {
        val tag = "PerplexityRepository"
        return try {
            Log.d(tag, "=== Starting Domain Filtering Test ===")
            
            Log.d(tag, "Step 1: Verifying trusted sources list")
            val trustedDomains = TrustedSources.DOMAINS
            Log.d(tag, "✓ Trusted domains count: ${trustedDomains.size}")
            trustedDomains.forEach { Log.d(tag, "  - $it") }
            
            Log.d(tag, "Step 2: Creating request with domain filter")
            val testQuery = "What are the health benefits of exercise?"
            val request = SonarApiRequest(
                messages = listOf(
                    SonarApiRequest.Message(
                        role = "user",
                        content = "Answer this question based only on trusted sources: $testQuery"
                    )
                ),
                model = ApiConfig.MODEL_SONAR,
                temperature = ApiConfig.DEFAULT_TEMPERATURE,
                maxTokens = ApiConfig.DEFAULT_MAX_TOKENS,
                searchDomainFilter = ApiConfig.DEFAULT_SEARCH_DOMAIN_FILTER
            )
            Log.d(tag, "✓ Request created with search_domain_filter")
            
            Log.d(tag, "Step 3: Sending request to API")
            val authHeader = "Bearer ${ApiConfig.API_KEY}"
            val response = apiService.verifyClaim(authHeader, request)
            Log.d(tag, "✓ Response received from API")
            
            Log.d(tag, "Step 4: Analyzing response for trusted domain citations")
            val content = response.choices.firstOrNull()?.message?.content ?: ""
            Log.d(tag, "Response length: ${content.length} characters")
            
            val citedUrls = response.citations ?: emptyList()
            Log.d(tag, "✓ URLs from API: ${citedUrls.size}")
            citedUrls.forEach { Log.d(tag, "  - $it") }
            
            Log.d(tag, "Step 5: Verifying citations are from trusted sources")
            val trustedCitations = mutableListOf<String>()
            val untrustedCitations = mutableListOf<String>()
            
            citedUrls.forEach { url ->
                val isTrusted = trustedDomains.any { domain -> url.contains(domain, ignoreCase = true) }
                if (isTrusted) {
                    trustedCitations.add(url)
                    Log.d(tag, "✓ TRUSTED: $url")
                } else {
                    untrustedCitations.add(url)
                    Log.d(tag, "✗ UNTRUSTED: $url")
                }
            }
            
            Log.d(tag, "Step 6: Checking for domain filter effectiveness")
            val responseContainsTrustedDomainNames = trustedDomains.any { domain ->
                content.contains(domain, ignoreCase = true)
            }
            Log.d(tag, "Response mentions trusted domains: $responseContainsTrustedDomainNames")
            
            val success = untrustedCitations.isEmpty()
            val message = if (success) {
                "Domain filtering verified successfully"
            } else {
                "Warning: Found citations from untrusted sources"
            }
            
            Log.d(tag, "=== Domain Filtering Test COMPLETE ===")
            TestResult(
                success = success,
                message = message,
                details = """Trusted domains in filter: ${trustedDomains.size}
                |URLs extracted from response: ${citedUrls.size}
                |Trusted citations: ${trustedCitations.size}
                |Untrusted citations: ${untrustedCitations.size}
                |Response mentions trusted sources: $responseContainsTrustedDomainNames
                |Test query: '$testQuery'
                |Tokens used: ${response.usage.promptTokens + response.usage.completionTokens}
                """.trimMargin()
            )
        } catch (e: Exception) {
            Log.e(tag, "=== Domain Filtering Test FAILED ===", e)
            Log.e(tag, "Error: ${e.message}")
            TestResult(
                success = false,
                message = "Domain filtering test failed",
                details = "${e::class.simpleName}: ${e.message}"
            )
        }
    }

    // Data class to encapsulate the fields that make up the test results
    data class TestResult(
        val success: Boolean,
        val message: String,
        val details: String
    )
}
