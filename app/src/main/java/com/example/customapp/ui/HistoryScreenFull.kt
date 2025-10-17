package com.example.customapp.ui

// Import packages required for functionality
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.customapp.data.model.VerificationResult
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.customapp.ui.theme.CustomAppTheme

// === Composables: to display the various UI components of the history screen ===

@Composable
// A composable to display the verification history screen
fun HistoryScreenFull(
    // Create a list to store past verification results
    historyList: List<VerificationResult>,
    // Make each item clickable and pass the result to the ResultDisplayScreen (to view it)
    onItemClick: (VerificationResult) -> Unit,
    onDelete: (Int) -> Unit
) {
    // Create a single column to display the history list
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display a heading for the history list
        Text(
            text = "Verification History",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // When the history list is empty, display an empty history state, otherwise display the history list using a LazyColumn to display the history list in a scrollable list
        if (historyList.isEmpty()) {
            EmptyHistoryState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Display each verification result in the history list. Set the key to the hash code of the result to ensure unique items
                items(
                    items = historyList,
                    key = { it.hashCode() }
                ) { result ->
                    HistoryItem(
                        result = result,
                        onItemClick = { onItemClick(result) },
                        onDelete = { onDelete(result.hashCode()) }
                    )
                }
            }
        }
    }
}

@Composable
// A composable to display a single verification result in the history list
fun HistoryItem(
    result: VerificationResult,
    onItemClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = result.claim.take(50) + if (result.claim.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBadge(rating = result.rating)
                    Text(
                        text = formatTimestamp(System.currentTimeMillis()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this verification?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
// Composable to display a rating badge for a verification result
fun RatingBadge(rating: VerificationResult.Rating) {
    val (label, color) = when (rating) {
        VerificationResult.Rating.MOSTLY_TRUE -> "True" to Color(0xFF4CAF50)
        VerificationResult.Rating.MIXED -> "Mixed" to Color(0xFFFFC107)
        VerificationResult.Rating.MOSTLY_FALSE -> "False" to Color(0xFFF44336)
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
// Composable to display an empty history state
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Empty",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Verification History",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start by verifying a claim to see your history here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// (Helper) function to format the timestamp shown in the history list

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// === Previews: to test the UI in the preview window ===
@Preview(showBackground = true, showSystemUi = true)
// Preview to show the history screen with some sample data
@Composable
fun HistoryScreenPreviewWithItems() {
    CustomAppTheme {
        HistoryScreenFull(
            historyList = listOf(
                VerificationResult(
                    claim = "The Earth is round",
                    rating = VerificationResult.Rating.MOSTLY_TRUE,
                    summary = "This claim is supported by scientific evidence.",
                    explanation = "Multiple observations confirm Earth's spherical shape.",
                    citations = listOf("https://www.nasa.gov")
                ),
                VerificationResult(
                    claim = "Coffee is bad for health",
                    rating = VerificationResult.Rating.MIXED,
                    summary = "Partially true - depends on consumption amount.",
                    explanation = "Moderate consumption has benefits, excessive consumption has risks.",
                    citations = listOf("https://www.healthline.com")
                ),
                VerificationResult(
                    claim = "Vaccines cause autism",
                    rating = VerificationResult.Rating.MOSTLY_FALSE,
                    summary = "This claim is false.",
                    explanation = "Multiple studies have found no link between vaccines and autism.",
                    citations = listOf("https://www.cdc.gov")
                )
            ),
            onItemClick = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
// Preview to show the history screen with no data
@Composable
fun HistoryScreenPreviewEmpty() {
    CustomAppTheme {
        HistoryScreenFull(
            historyList = emptyList(),
            onItemClick = {},
            onDelete = {}
        )
    }
}
