// ui/ResultDisplayScreen.kt

/**
 * Screen for displaying verification results and source citations.
 *
 * This screen shows:
 * - Verification status (True/False/Misleading/Unable to Verify)
 * - Summary of findings
 * - Detailed explanation
 * - List of source citations with clickable links
 * - Option to return to the input screen
 *
 * The UI updates reactively based on the provided VerificationResult.
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
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.customapp.ui.theme.CustomAppTheme
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.ui.text.style.TextAlign
import com.example.customapp.ui.theme.*

// -------------------------------------------------------------------------------------------------
// Composables
// -------------------------------------------------------------------------------------------------

// Composable to display the result of a verification query
@Composable
fun ResultDisplayScreen(
    result: VerificationResult,
    onNewQuery: () -> Unit
) {
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

        // For each sub-section reuse the SectionHeader and SectionContent composables
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

// Reusable composable for displaying citations
@Composable
fun CitationsList(
    citations: List<String>,
    uriHandler: androidx.compose.ui.platform.UriHandler
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        citations.forEach { citation ->
            Text(
                text = citation,
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.Underline
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable {
                        try {
                            uriHandler.openUri(citation)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            )
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

// Helper function to create preview data
private fun createPreviewResult(
    claim: String,
    rating: VerificationResult.Rating,
    summary: String,
    explanation: String,
    citations: List<String>
) = VerificationResult(
    id = 0,
    claim = claim,
    rating = rating,
    summary = summary,
    explanation = explanation,
    citations = citations,
    timestamp = System.currentTimeMillis()
)


// -------------------------------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResultDisplayScreenPreviewTrue() {
    CustomAppTheme {
        ResultDisplayScreen(
            result = createPreviewResult(
                claim = "The Earth is round",
                rating = VerificationResult.Rating.TRUE,
                summary = "This claim is supported by scientific evidence and observations.",
                explanation = "The spherical shape of the Earth has been confirmed through multiple methods including satellite imagery, physics, and direct observation. This is one of the most well-established facts in science.",
                citations = listOf(
                    "https://www.nasa.gov/earth",
                    "https://www.britannica.com/science/Earth"
                )
            ),
            onNewQuery = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResultDisplayScreenPreviewFalse() {
    CustomAppTheme {
        ResultDisplayScreen(
            result = createPreviewResult(
                claim = "Vaccines cause autism",
                rating = VerificationResult.Rating.FALSE,
                summary = "This claim is false. Multiple large-scale studies have found no link between vaccines and autism.",
                explanation = "The original study claiming a vaccine-autism link has been thoroughly discredited and retracted. Numerous subsequent studies involving millions of children have found no connection between vaccines and autism. The scientific consensus is clear that vaccines do not cause autism.",
                citations = listOf(
                    "https://www.cdc.gov/vaccinesafety/concerns/autism.html",
                    "https://www.who.int/news-room/fact-sheets/detail/immunization-vaccines-and-biologicals"
                )
            ),
            onNewQuery = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResultDisplayScreenPreviewMisleading() {
    CustomAppTheme {
        ResultDisplayScreen(
            result = createPreviewResult(
                claim = "Coffee is bad for your health",
                rating = VerificationResult.Rating.MISLEADING,
                summary = "This claim is misleading. While excessive coffee consumption can have negative effects, moderate consumption has been shown to have health benefits.",
                explanation = "Research shows that moderate coffee consumption (3-5 cups per day) is associated with various health benefits, including reduced risk of certain diseases. However, excessive consumption can lead to anxiety, sleep issues, and other problems. The health effects depend on individual factors and consumption amount.",
                citations = listOf(
                    "https://www.healthline.com/nutrition/coffee-health-benefits",
                    "https://www.mayoclinic.org/healthy-lifestyle/nutrition-and-healthy-eating/in-depth/caffeine/art-20045431"
                )
            ),
            onNewQuery = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResultDisplayScreenPreviewUnableToVerify() {
    CustomAppTheme {
        ResultDisplayScreen(
            result = createPreviewResult(
                claim = "Ancient aliens built the pyramids",
                rating = VerificationResult.Rating.UNABLE_TO_VERIFY,
                summary = "This claim cannot be verified with current evidence.",
                explanation = "There is no credible evidence to support the claim that ancient aliens built the pyramids. However, it's difficult to definitively prove a negative. The mainstream archaeological consensus is that the pyramids were built by ancient Egyptians using sophisticated engineering techniques for their time.",
                citations = listOf(
                    "https://www.nationalgeographic.com/history/archaeology/giza-pyramids/",
                    "https://www.smithsonianmag.com/history/ancient-egypt-shipping-mining-farming-economy-pyramids-180956619/"
                )
            ),
            onNewQuery = {}
        )
    }
}
