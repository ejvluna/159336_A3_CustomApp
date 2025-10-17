// Import the required packages for naming the app and activity
package com.example.customapp

// Import the required packages for the app theme and activity
import com.example.customapp.ui.theme.CustomAppTheme
import com.example.customapp.ui.QueryInputScreenFull
import com.example.customapp.ui.HistoryScreenFull
import com.example.customapp.data.model.VerificationResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

// Define the main activity class
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CustomApp()
                }
            }
        }
    }
}

// Define the custom app composable function
@Composable
fun CustomApp() {
    var selectedScreen by remember { mutableStateOf(Screen.QUERY) }
    var historyList by remember { mutableStateOf<List<VerificationResult>>(emptyList()) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Query") },
                    label = { Text("Verify") },
                    selected = selectedScreen == Screen.QUERY,
                    onClick = { selectedScreen = Screen.QUERY }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.History, contentDescription = "History") },
                    label = { Text("History") },
                    selected = selectedScreen == Screen.HISTORY,
                    onClick = { selectedScreen = Screen.HISTORY }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedScreen) {
                Screen.QUERY -> QueryInputScreenFull(
                    onSubmit = { query ->
                        // Handle query submission
                    }
                )
                Screen.HISTORY -> HistoryScreenFull(
                    historyList = historyList,
                    onItemClick = { result ->
                        // Handle item click
                    },
                    onDelete = { id ->
                        // Handle delete
                    }
                )
            }
        }
    }
}

// Define the screen enum class: the different screens of the app
enum class Screen {
    QUERY,
    HISTORY
}

// Define the query input screen composable function
@Composable
fun QueryInputScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verify a Claim",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Text("Query input screen placeholder")
    }
}

// Define the history screen composable function
@Composable
fun HistoryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Verification History",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text("History screen placeholder")
    }
}