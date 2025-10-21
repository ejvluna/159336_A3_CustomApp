// /data/api/RetrofitClient.kt
package com.example.customapp.data.api

// Import required packages to perform API calls and handle HTTP requests
import com.example.customapp.config.ApiConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton object that configures and provides instances of Retrofit and OkHttpClient for API communication
object RetrofitClient {
    // OkHttpClient configured with 30-second timeouts for connection, read, and write operations; retries on connection failure
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    // Retrofit instance configured with the Perplexity API base URL and Gson converter for JSON serialization/deserialization
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
