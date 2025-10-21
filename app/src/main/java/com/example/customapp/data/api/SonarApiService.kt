// /data/api/SonarApiService.kt
package com.example.customapp.data.api

// Import required packages to perform API calls and handle HTTP requests
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Retrofit interface that defines the HTTP endpoint for the Perplexity Sonar API.
interface SonarApiService {
    // POST request to the "chat/completions" endpoint that sends a claim for fact-checking and returns a verification result

    @POST("chat/completions")
    // Function to verify a claim using the Sonar API
    suspend fun verifyClaim(
        // Bearer token for API authentication;
        @Header("Authorization") authHeader: String,
        // SonarApiRequest containing the claim and search parameters
        @Body request: SonarApiRequest
    ): SonarApiResponse
}
