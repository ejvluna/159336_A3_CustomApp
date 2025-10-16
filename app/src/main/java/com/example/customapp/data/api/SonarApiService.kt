package com.example.customapp.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SonarApiService {
    @POST("chat/completions")
    suspend fun verifyClaim(
        @Header("Authorization") authHeader: String,
        @Body request: SonarApiRequest
    ): SonarApiResponse
}
