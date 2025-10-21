// ui/QueryInputScreen.kt
package com.example.customapp.ui

// Import packages required for functionality
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.customapp.ui.theme.*

// Composable to display the query input screen
@Composable
fun QueryInputScreen(
    onSubmit: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    // State variables for the query input screen
    var query by remember { mutableStateOf("") }
    val maxCharacters = 500
    val charCount = query.length
    var showEmptyError by remember { mutableStateOf(false) }
    var showMaxLengthError by remember { mutableStateOf(false) }

    // Display the query input screen using a column layout
    Column(
        // Set modifiers to fill the max size of the screen, add padding on all sides, and center align the content
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Set text for the query input screen
        Text(
            text = "Verify a Claim",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        // Use an outlined text field for the query input with a label and placeholder
        OutlinedTextField(
            value = query,
            onValueChange = { newValue ->
                if (newValue.length <= maxCharacters) {
                    query = newValue
                    showEmptyError = false
                    showMaxLengthError = false
                } else {
                    showMaxLengthError = true
                }
            },
            isError = showEmptyError || showMaxLengthError,
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
        // Display the character count and progress indicator
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
        // Show error message if needed
        if (showEmptyError) {
            Text(
                text = "Please enter a claim to verify",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else if (showMaxLengthError) {
            Text(
                text = "Maximum $maxCharacters characters allowed",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Button for the user to submit the query
        Button(
            onClick = {
                if (query.isBlank()) {
                    showEmptyError = true
                } else if (query.length > maxCharacters) {
                    showMaxLengthError = true
                } else {
                    onSubmit(query)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
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
}

// -------------------------------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QueryInputScreenPreview() {
    CustomAppTheme {
        QueryInputScreen(
            onSubmit = {},
            isLoading = false,
            errorMessage = null
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
            errorMessage = null
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
            errorMessage = "Network error: Failed to connect to API"
        )
    }
}
