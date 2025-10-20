// /data/api/SonarApiService.kt
package com.example.customapp.data.api

// Import required packages to perform API calls and handle HTTP requests
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Interface to define the API service for the Sonar API
interface SonarApiService {
    @POST("chat/completions")
    suspend fun verifyClaim(
        @Header("Authorization") authHeader: String,
        @Body request: SonarApiRequest
    ): SonarApiResponse
}
