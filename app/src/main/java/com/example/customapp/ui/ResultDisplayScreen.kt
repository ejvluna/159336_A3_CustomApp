package com.example.customapp.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import com.example.customapp.data.model.VerificationResult
import androidx.compose.ui.tooling.preview.Preview
import com.example.customapp.ui.theme.CustomAppTheme

@Composable
fun ResultDisplayScreen(
    result: VerificationResult,
    onNewQuery: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Verification Result",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        StatusIndicator(rating = result.rating)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Claim",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = result.claim,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = result.summary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Explanation",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = result.explanation,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (result.citations.isNotEmpty()) {
            Text(
                text = "Citations",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                result.citations.forEach { citation ->
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNewQuery,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Verify Another Claim")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatusIndicator(rating: VerificationResult.Rating) {
    val (icon, color, label) = when (rating) {
        VerificationResult.Rating.MOSTLY_TRUE -> Triple(
            Icons.Filled.CheckCircle,
            Color(0xFF4CAF50),
            "Mostly True"
        )
        VerificationResult.Rating.MIXED -> Triple(
            Icons.Filled.Warning,
            Color(0xFFFFC107),
            "Mixed"
        )
        VerificationResult.Rating.MOSTLY_FALSE -> Triple(
            Icons.Filled.Close,
            Color(0xFFF44336),
            "Mostly False"
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResultDisplayScreenPreviewTrue() {
    CustomAppTheme {
        ResultDisplayScreen(
            result = VerificationResult(
                claim = "The Earth is round",
                rating = VerificationResult.Rating.MOSTLY_TRUE,
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
fun ResultDisplayScreenPreviewMixed() {
    CustomAppTheme {
        ResultDisplayScreen(
            result = VerificationResult(
                claim = "Coffee is bad for your health",
                rating = VerificationResult.Rating.MIXED,
                summary = "This claim is partially true. While excessive coffee consumption can have negative effects, moderate consumption has been shown to have health benefits.",
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
fun ResultDisplayScreenPreviewFalse() {
    CustomAppTheme {
        ResultDisplayScreen(
            result = VerificationResult(
                claim = "Vaccines cause autism",
                rating = VerificationResult.Rating.MOSTLY_FALSE,
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
