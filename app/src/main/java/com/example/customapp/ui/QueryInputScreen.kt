package com.example.customapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.customapp.ui.theme.CustomAppTheme

@Composable
fun QueryInputScreenFull(
    onSubmit: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var query by remember { mutableStateOf("") }
    val maxCharacters = 500
    val charCount = query.length

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Verify a Claim",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$charCount / $maxCharacters",
                style = MaterialTheme.typography.labelSmall,
                color = if (charCount >= maxCharacters * 0.9) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (charCount >= maxCharacters * 0.8) {
                LinearProgressIndicator(
                    progress = { charCount.toFloat() / maxCharacters },
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSubmit(query) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = query.isNotBlank() && !isLoading
        ) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QueryInputScreenPreview() {
    CustomAppTheme {
        QueryInputScreenFull(
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
        QueryInputScreenFull(
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
        QueryInputScreenFull(
            onSubmit = {},
            isLoading = false,
            errorMessage = "Network error: Failed to connect to API"
        )
    }
}
