package com.example.customapp.data

import com.example.customapp.config.ApiConfig
import com.example.customapp.data.api.SonarApiRequest
import com.example.customapp.data.api.SonarApiService
import com.example.customapp.data.database.ClaimHistoryDao
import com.example.customapp.data.database.ClaimHistoryEntity
import com.example.customapp.data.model.VerificationResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PerplexityRepository(
    private val apiService: SonarApiService,
    private val claimHistoryDao: ClaimHistoryDao
) {
    private val gson = Gson()

    suspend fun verifyQuery(query: String): VerificationResult {
        return try {
            val request = SonarApiRequest(
                messages = listOf(
                    SonarApiRequest.Message(
                        role = "user",
                        content = "Fact-check the following claim and provide a rating (MOSTLY_TRUE, MIXED, or MOSTLY_FALSE), summary, explanation, and any citations: $query"
                    )
                ),
                model = ApiConfig.MODEL_SONAR,
                temperature = ApiConfig.DEFAULT_TEMPERATURE,
                maxTokens = ApiConfig.DEFAULT_MAX_TOKENS,
                searchDomainFilter = ApiConfig.DEFAULT_SEARCH_DOMAIN_FILTER
            )

            val authHeader = "Bearer ${ApiConfig.API_KEY}"
            val response = apiService.verifyClaim(authHeader, request)

            val content = response.choices.firstOrNull()?.message?.content ?: ""
            parseApiResponse(query, content)
        } catch (e: Exception) {
            VerificationResult(
                claim = query,
                rating = VerificationResult.Rating.MIXED,
                summary = "Error during verification",
                explanation = e.message ?: "Unknown error occurred",
                citations = emptyList()
            )
        }
    }

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

    suspend fun deleteQuery(id: Int) {
        claimHistoryDao.deleteClaimById(id)
    }

    private fun parseApiResponse(
        claim: String,
        content: String
    ): VerificationResult {
        val rating = when {
            content.contains("mostly true", ignoreCase = true) -> VerificationResult.Rating.MOSTLY_TRUE
            content.contains("mostly false", ignoreCase = true) -> VerificationResult.Rating.MOSTLY_FALSE
            else -> VerificationResult.Rating.MIXED
        }

        val citations = extractCitations(content)

        return VerificationResult(
            claim = claim,
            rating = rating,
            summary = content.take(200),
            explanation = content,
            citations = citations
        )
    }

    private fun extractCitations(content: String): List<String> {
        val urlRegex = """https?://[^\s]+""".toRegex()
        return urlRegex.findAll(content).map { it.value }.toList()
    }

    private fun parseCitations(citationsJson: String): List<String> {
        return try {
            gson.fromJson(citationsJson, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
