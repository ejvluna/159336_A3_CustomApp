// ui/ResultDisplayScreen.kt

/**
 * Screen for displaying verification results and source citations.
 *
 * This screen shows:
 * - Verification status (True/False/Misleading/Unable to Verify)
 * - Summary of findings
 * - Detailed explanation
 * - List of source citations with clickable links
 * - Provides navigation back to query screen
 *
 * Uses QueryViewModel for state management and handles one-time events like navigation and URL opening.
 */

package com.example.customapp.ui

// Import packages required for functionality
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.ui.Alignment
import com.example.customapp.data.model.VerificationResult
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.ui.text.style.TextAlign
import com.example.customapp.ui.theme.*

// -------------------------------------------------------------------------------------------------
// Composable
// -------------------------------------------------------------------------------------------------

// Composable to display the result of a verification query
@Composable
fun ResultDisplayScreen(
    result: VerificationResult,
    onNewQuery: () -> Unit
) {
    // Use the provided callbacks for navigation; no ViewModel needed here

    // Get and store the current uri handler and scroll state for smooth scrolling
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()

    // Create a column to hold the result display screen and use spacers to separate sections
    Column(
        // Set modifiers to fill the max size of the screen, enable vertical scrolling, and add padding on all sides
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp)
    ) {
        // Add a text field to hold the heading
        Text(
            text = "Verification Result",
            style = MaterialTheme.typography.headlineMedium,
            // Set modifiers to fill the max width of the screen, add padding on all sides, and center align the text
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            textAlign = TextAlign.Center
        )
        // Display the status indicator composable with the rating from the result
        StatusIndicator(rating = result.rating)
        Spacer(modifier = Modifier.height(20.dp))

        // For each sub-section reuse the SectionHeader and SectionContent composable
        SectionHeader(icon = Icons.Filled.Search, title = "Claim")
        // Display the claim text in a surface with padding to make it visually distinct from the rest of the screen
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        ) {
            SectionContent(text = result.claim, textStyle = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader(icon = Icons.Filled.Lightbulb, title = "Summary")
        SectionContent(text = result.summary, textStyle = MaterialTheme.typography.bodyMedium)

        SectionHeader(icon = Icons.Filled.Description, title = "Explanation")
        SectionContent(text = result.explanation, textStyle = MaterialTheme.typography.bodySmall)

        // When citations are not empty, display the citations list
        if (result.citations.isNotEmpty()) {
            SectionHeader(icon = Icons.Filled.Link, title = "Citations")
            CitationsList(citations = result.citations, uriHandler = uriHandler)
        }
        // Call the Button composable to display a button to verify another claim
        Button(
            onClick = onNewQuery,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify Another Claim")
        }
    }
}

// Reusable composable for section headers layout and formatting
@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    // Add a row to display the icon and title
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Add an icon to the section header
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Add the title to the section header
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

// Reusable composable for section content text formatting
@Composable
fun SectionContent(text: String, textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium) {
    Text(
        text = text,
        style = textStyle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    )
}

// Reusable composable for displaying citations with title, URL, and publication date
// Updated May 2025: Now displays full citation information from search_results API field
@Composable
fun CitationsList(
    citations: List<VerificationResult.Citation>,
    uriHandler: androidx.compose.ui.platform.UriHandler
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        citations.forEach { citation ->
            // Create a clickable card for each citation with title, URL, and optional date
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        try {
                            uriHandler.openUri(citation.url)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Display the citation title
                    Text(
                        text = citation.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    // Display the citation URL
                    Text(
                        text = citation.url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    // Display the publication date if available
                    if (!citation.date.isNullOrEmpty()) {
                        Text(
                            text = "Published: ${citation.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

// Composable for displaying the status indicator
@Composable
fun StatusIndicator(rating: VerificationResult.Rating) {
    val (icon, color, label) = when (rating) {
        VerificationResult.Rating.TRUE -> Triple(
            Icons.Filled.CheckCircle,
            MaterialTheme.colorScheme.statusTrue,
            "True"
        )
        VerificationResult.Rating.FALSE -> Triple(
            Icons.Filled.Close,
            MaterialTheme.colorScheme.statusFalse,
            "False"
        )
        VerificationResult.Rating.MISLEADING -> Triple(
            Icons.Filled.Warning,
            MaterialTheme.colorScheme.statusMisleading,
            "Misleading"
        )
        VerificationResult.Rating.UNABLE_TO_VERIFY -> Triple(
            Icons.Filled.Info,
            MaterialTheme.colorScheme.statusUnverified,
            "Unable to Verify"
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall,
                color = color
            )
        }
    }
}

