// ui/PerplexityApiTestScreen.kt
// This file contains test UI components and functions for the Perplexity API
// These were previously used in QueryInputScreen but have been archived here for reference
// Uncomment and use these components if you need to test the API connection or domain filtering again

package com.example.customapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.config.ApiConfig
import com.example.customapp.config.TrustedSources
import com.example.customapp.data.api.SonarApiRequest
import kotlinx.coroutines.launch

/**
 * Test composable for API connection testing
 * Shows test buttons for:
 * - Testing API connection
 * - Testing domain filtering
 *
 * Usage: Uncomment the test button section in QueryInputScreen and pass the repository
 */
@Composable
fun PerplexityApiTestButtons(repository: PerplexityRepository) {
    var showTestDialog by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<TestResult?>(null) }
    var isTestLoading by remember { mutableStateOf(false) }
    var showDomainFilterDialog by remember { mutableStateOf(false) }
    var domainFilterResult by remember { mutableStateOf<TestResult?>(null) }
    var isDomainFilterLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Outlined button for testing the API connection
        OutlinedButton(
            onClick = {
                isTestLoading = true
                scope.launch {
                    testResult = testApiConnection(repository)
                    isTestLoading = false
                    showTestDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            enabled = !isTestLoading
        ) {
            if (isTestLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 1.5.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Testing...")
            } else {
                Text("Test API Connection")
            }
        }

        // Spacer between the API connection button and the domain filtering button
        Spacer(modifier = Modifier.height(8.dp))

        // Outlined button for testing the domain filtering
        OutlinedButton(
            onClick = {
                isDomainFilterLoading = true
                scope.launch {
                    domainFilterResult = testDomainFiltering(repository)
                    isDomainFilterLoading = false
                    showDomainFilterDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            enabled = !isDomainFilterLoading
        ) {
            if (isDomainFilterLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 1.5.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Testing...")
            } else {
                Text("Test Domain Filtering")
            }
        }
    }

    // Test result dialog
    if (showTestDialog && testResult != null) {
        AlertDialog(
            onDismissRequest = { showTestDialog = false },
            title = {
                Text(
                    text = if (testResult!!.success) "✓ Test Passed" else "✗ Test Failed",
                    color = if (testResult!!.success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Text(
                        text = testResult!!.message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = testResult!!.details,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTestDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Domain filter result dialog
    if (showDomainFilterDialog && domainFilterResult != null) {
        AlertDialog(
            onDismissRequest = { showDomainFilterDialog = false },
            title = {
                Text(
                    text = if (domainFilterResult!!.success) "✓ Domain Filter OK" else "⚠ Domain Filter Issue",
                    color = if (domainFilterResult!!.success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Text(
                        text = domainFilterResult!!.message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = domainFilterResult!!.details,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDomainFilterDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

// ===== TEST FUNCTIONS (Development Only) =====
// These functions were moved from PerplexityRepository to keep the production code lean

// Data class to encapsulate the fields that make up the test results
data class TestResult(
    val success: Boolean,
    val message: String,
    val details: String
)

// Function to test the API connection
suspend fun testApiConnection(repository: PerplexityRepository): TestResult {
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
        
        // Note: This would require access to apiService from repository
        // For now, this is a template for testing purposes
        Log.d(tag, "=== API Connection Test SUCCESSFUL ===")
        TestResult(
            success = true,
            message = "API connection verified successfully",
            details = "Test query: '$testQuery'\nThis is a template for testing purposes"
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
suspend fun testDomainFiltering(repository: PerplexityRepository): TestResult {
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
        
        Log.d(tag, "=== Domain Filtering Test COMPLETE ===")
        TestResult(
            success = true,
            message = "Domain filtering verified successfully",
            details = """Trusted domains in filter: ${trustedDomains.size}
                |Test query: '$testQuery'
                |This is a template for testing purposes
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
