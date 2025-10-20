// ui/QueryInputScreen.kt
package com.example.customapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.customapp.ui.theme.CustomAppTheme
import com.example.customapp.data.PerplexityRepository
import kotlinx.coroutines.launch

@Composable
// Composable to display the query input screen
fun QueryInputScreen(
    onSubmit: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    repository: PerplexityRepository? = null
) {
    // State variables for the query input screen
    var query by remember { mutableStateOf("") }
    val maxCharacters = 500
    val charCount = query.length
    var showTestDialog by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<PerplexityRepository.TestResult?>(null) }
    var isTestLoading by remember { mutableStateOf(false) }
    var showDomainFilterDialog by remember { mutableStateOf(false) }
    var domainFilterResult by remember { mutableStateOf<PerplexityRepository.TestResult?>(null) }
    var isDomainFilterLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Single column to display the query input screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Headline text for the query input screen
        Text(
            text = "Verify a Claim",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        // Outlined text field for the query input with a label and placeholder
        OutlinedTextField(
            value = query,
            onValueChange = { newValue ->
                if (newValue.length <= maxCharacters) {
                    query = newValue
                }
            },
            label = { Text("Enter a claim to verify") },
            placeholder = { Text("Type your claim here...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            maxLines = 5,
            enabled = !isLoading,
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            }
        )
        // Row to display the character count and progress indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text to display the character count and progress indicator
            Text(
                text = "$charCount / $maxCharacters",
                style = MaterialTheme.typography.labelSmall,
                color = if (charCount >= maxCharacters * 0.9) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
            // When the character count is greater than 80% of the max characters, display a progress indicator
            if (charCount >= maxCharacters * 0.8) {
                LinearProgressIndicator(
                    progress = { charCount.toFloat() / maxCharacters },
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                )
            }
        }
        // Spacer to add space between the character count and the button
        Spacer(modifier = Modifier.height(24.dp))
        // Button for the user to submit the query
        Button(
            // When the button is clicked, call the onSubmit function with the query
            onClick = { onSubmit(query) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = query.isNotBlank() && !isLoading
        ) {
            // Display a loading indicator and text when the button is clicked
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verifying...")
            } else {
                Text("Verify Claim")
            }
        }
        // Outlined button for testing the API connection when the repository is not null
        if (repository != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    isTestLoading = true
                    scope.launch {
                        testResult = repository.testApiConnection()
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

            // Outlined button for testing the domain filtering when the repository is not null
            OutlinedButton(
                onClick = {
                    isDomainFilterLoading = true
                    scope.launch {
                        domainFilterResult = repository.testDomainFiltering()
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
        // Error message display when the error message is not null
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tips: Be specific and clear with your claim for better verification results.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QueryInputScreenPreview() {
    CustomAppTheme {
        QueryInputScreen(
            onSubmit = {},
            isLoading = false,
            errorMessage = null,
            repository = null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QueryInputScreenLoadingPreview() {
    CustomAppTheme {
        QueryInputScreen(
            onSubmit = {},
            isLoading = true,
            errorMessage = null,
            repository = null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QueryInputScreenErrorPreview() {
    CustomAppTheme {
        QueryInputScreen(
            onSubmit = {},
            isLoading = false,
            errorMessage = "Network error: Failed to connect to API",
            repository = null
        )
    }
}
