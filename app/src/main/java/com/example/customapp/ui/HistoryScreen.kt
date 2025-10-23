// ui/HistoryScreen.kt
/**
 * History Screen - Displays a scrollable list of past verification results.
 *
 * This screen uses a ViewModel to manage the list of past claims from Room database.
 * The list is automatically updated via Flow when the database changes.
 */

package com.example.customapp.ui

// Import packages for Jetpack Compose UI components, layout, lazy lists, icons, state management, and data models
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.example.customapp.data.model.VerificationResult
import com.example.customapp.data.PerplexityRepository
import java.text.SimpleDateFormat
import java.util.*
import com.example.customapp.ui.theme.*


// -------------------------------------------------------------------------------------------------
// Composable: to display the various UI components of the history screen
// -------------------------------------------------------------------------------------------------

@Composable
// A composable to display a scrollable list of all past verification results with options to view or delete each item
fun HistoryScreen(
    // Repository for data access
    repository: PerplexityRepository,
    // Callback invoked when a history item is clicked to view the full verification result
    onItemClick: (VerificationResult) -> Unit
) {
    // Create the ViewModel with the provided repository (simple instantiation, no factory needed)
    val viewModel = remember { HistoryViewModel(repository) }
    
    // Observe the history list from the ViewModel
    val historyList by viewModel.historyFlow.collectAsState()
    
    // Observe the delete error state from the ViewModel
    val deleteError by viewModel.deleteError.collectAsState()

    // Simple loading state to prevent empty state flash on first load
    var isInitialLoading by remember { mutableStateOf(true) }

    // Dismiss loading indicator after 300ms (enough time for database to emit)
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        isInitialLoading = false
    }

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
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        // Show loading indicator during initial load, then show list or empty state
        if (isInitialLoading) {
            // Simple centered loading spinner
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (historyList.isEmpty()) {
            EmptyHistoryState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Call HistoryItem composable to display each verification result in the history list as a clickable/deletable item; key ensures efficient list updates
                items(
                    items = historyList,
                    key = { it.id }
                ) { result ->
                    HistoryItem(
                        result = result,
                        onItemClick = { onItemClick(result) },
                        onDelete = { viewModel.deleteQuery(result.id) },
                        deleteError = deleteError,
                        onClearError = { viewModel.clearDeleteError() }
                    )
                }
            }
        }
    }
}

// Composable that displays a history item as a clickable/deletable item
@Composable
fun HistoryItem(
    result: VerificationResult,
    onItemClick: () -> Unit,
    onDelete: () -> Unit,
    deleteError: String? = null,
    onClearError: () -> Unit = {}
) {
    // Track whether the delete confirmation dialog is visible
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Show error message if delete error exists and is not empty
    var showDeleteError by remember { mutableStateOf(false) }

    // Update showDeleteError when deleteError changes
    LaunchedEffect(deleteError) {
        if (deleteError != null && deleteError.isNotEmpty()) {
            showDeleteError = true
        }
    }

    // Call the ClickableHistoryCard composable to display a clickable card for each history item
    ClickableHistoryCard(
        onClick = onItemClick,
        content = {
            // Row to hold the history item content and delete button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                // Use SpaceBetween to space out the history item content and delete button
                //horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call the HistoryItemContent composable to display the claim preview, rating, and timestamp inside the clickable card (far left)
                HistoryItemContent(
                    modifier = Modifier.weight(1f),
                    claim = result.claim,
                    rating = result.rating,
                    timestamp = result.timestamp
                )
                // Call the DeleteButton composable to display the delete button inside the clickable card (far right)
                DeleteButton(onClick = { showDeleteConfirm = true })
            }
        }
    )
    // Call the DeleteConfirmationDialog composable to display the delete confirmation dialog
    DeleteConfirmationDialog(
        showDialog = showDeleteConfirm,
        onDismiss = { showDeleteConfirm = false },
        onConfirm = {
            onDelete()
            showDeleteConfirm = false
        }
    )

    // Show error message if delete fails
    if (showDeleteError && deleteError != null && deleteError.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deleteError,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        showDeleteError = false
                        onClearError()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss error",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Composable that displays a clickable card for a history item
@Composable
private fun ClickableHistoryCard(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    // Use a Surface composable to create a card with a clickable area and rounded corners
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        // Call the content composable to display the history item content
        content()
    }
}

// Composable that displays the claim preview, rating, and timestamp
@Composable
private fun HistoryItemContent(
    modifier: Modifier = Modifier,
    claim: String,
    rating: VerificationResult.Rating,
    timestamp: Long
) {
    // Column to hold the history item content
    Column(
        modifier = modifier
            .padding(end = 12.dp)
    ) {
        // Call the TruncatedClaimText composable to display the claim preview
        TruncatedClaimText(claim = claim)
        Spacer(modifier = Modifier.height(4.dp))
        // Call the VerificationMetadata composable to display the rating and timestamp
        VerificationMetadata(rating = rating, timestamp = timestamp)
    }
}

// Composable that displays the claim preview
@Composable
private fun TruncatedClaimText(claim: String) {
    // Display the claim preview (up to 50 characters) in a medium text style with the surface variant color
    Text(
        text = claim.take(50) + if (claim.length > 50) "..." else "",
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1
    )
}

// Composable that displays the metadata for a verification result (rating and timestamp)
@Composable
private fun VerificationMetadata(
    rating: VerificationResult.Rating,
    timestamp: Long
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Call the RatingBadge composable to display the rating badge
        RatingBadge(rating = rating)
        // Call the TimestampText composable to display the timestamp
        TimestampText(timestamp = timestamp)
    }
}

@Composable
// Composable to display a rating badge for a verification result
fun RatingBadge(rating: VerificationResult.Rating) {
    val (label, color) = when (rating) {
        VerificationResult.Rating.TRUE -> "True" to MaterialTheme.colorScheme.statusTrue
        VerificationResult.Rating.FALSE -> "False" to MaterialTheme.colorScheme.statusFalse
        VerificationResult.Rating.MISLEADING -> "Misleading" to MaterialTheme.colorScheme.statusMisleading
        VerificationResult.Rating.UNABLE_TO_VERIFY -> "Unverified" to MaterialTheme.colorScheme.statusUnverified
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

// Composable that displays the timestamp for a verification result
@Composable
private fun TimestampText(timestamp: Long) {
    // Get the formatted timestamp using SimpleDateFormat and remember to avoid recomposition
    val formattedTime = remember(timestamp) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }
    // Display the timestamp in a small text style with the surface variant color
    Text(
        text = formattedTime,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun DeleteButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
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

@Composable
private fun DeleteConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    // Display the delete confirmation dialog if showDialog is true
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this verification?") },
            // Call the confirmButton composable to display the delete button
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            // Call the dismissButton composable to display the cancel button
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
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


// -------------------------------------------------------------------------------------------------
// Preview: to test the UI in the preview window
// -------------------------------------------------------------------------------------------------

// Note: Preview requires a mock repository - skipping for now as it requires full dependency setup
// To preview, you can temporarily create a mock PerplexityRepository or use the old parameter-based approach
// @Preview(showBackground = true, showSystemUi = true)
// @Composable
// fun HistoryScreenPreviewWithItems() {
//     CustomAppTheme {
//         HistoryScreen(
//             repository = mockRepository,
//             onItemClick = {}
//         )
//     }
// }

// Note: Preview requires a mock repository - skipping for now as it requires full dependency setup
// @Preview(showBackground = true, showSystemUi = true)
// @Composable
// fun HistoryScreenPreviewEmpty() {
//     CustomAppTheme {
//         HistoryScreen(
//             repository = mockRepository,
//             onItemClick = {}
//         )
//     }
// }
