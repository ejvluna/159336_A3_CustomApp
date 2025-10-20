// /data/api/RetrofitClient.kt
package com.example.customapp.data.api

// Import required packages to perform API calls and handle HTTP requests
import com.example.customapp.config.ApiConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Object to handle API calls and HTTP requests
object RetrofitClient {
    // Create an instance of OkHttpClient with timeout settings and lazy initialization
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    // Create and store an instance of Retrofit for API calls
    val retrofit: Retrofit by lazy {
        // Call the builder method to create a new instance of Retrofit
        Retrofit.Builder()
            // Set the base URL and add the Gson converter factory for JSON parsing
            .baseUrl(ApiConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            // Return the Retrofit instance
            .build()
    }
    // Create and store an instance of SonarApiService for API calls
    val sonarApiService: SonarApiService by lazy {
        retrofit.create(SonarApiService::class.java)
    }
}
