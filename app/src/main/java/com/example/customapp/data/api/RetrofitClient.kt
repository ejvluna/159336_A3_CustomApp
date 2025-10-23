// /data/api/RetrofitClient.kt

/**
 * Retrofit client configuration for the Verifica app's API communication.
 *
 * This singleton object provides:
 * - Pre-configured OkHttpClient with timeouts and retry logic
 * - Retrofit instance with Gson converter
 * - Base URL configuration from ApiConfig
 * - Automatic JSON serialization/deserialization
 *
 * The client is lazily initialized and shared across the app for efficient network resource usage.
 */
package com.example.customapp.data.api

// Import required packages to perform API calls and handle HTTP requests
import com.example.customapp.config.ApiConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton object that configures and provides single instances of Retrofit and OkHttpClient that are reused throughout app lifetime for API communication
object RetrofitClient {
    // Custom OkHttpClient with 30s timeouts and connection retry enabled.
    // Connection pooling is automatic - OkHttp reuses connections for multiple requests
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    // Retrofit instance using the base URL from ApiConfig and a Gson converter.
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    // SonarApiService instance created from Retrofit for making API requests to the Perplexity Sonar API
    val sonarApiService: SonarApiService by lazy {
        retrofit.create(SonarApiService::class.java)
    }
}
