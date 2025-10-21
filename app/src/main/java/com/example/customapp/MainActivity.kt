// Import the required packages for naming the app and activity
package com.example.customapp

// Import the required packages for the app theme and activity
import com.example.customapp.ui.theme.CustomAppTheme
import com.example.customapp.ui.QueryInputScreen
import com.example.customapp.ui.HistoryScreen
import com.example.customapp.ui.ResultDisplayScreen
import com.example.customapp.data.model.VerificationResult
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.data.api.RetrofitClient
import com.example.customapp.data.database.AppDatabase
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
import kotlinx.coroutines.launch

// Class to encapsulate the main app logic
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call the CustomApp composable to display the main app UI
                    CustomApp()
                }
            }
        }
    }
}

// Composable to display the main app UI (entry point)
@Composable
fun CustomApp() {
    // Create state variables needed to track the app state
    var selectedScreen by remember { mutableStateOf(Screen.QUERY) }
    var currentResult by remember { mutableStateOf<VerificationResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Get and store the app context, coroutine scope, and repository
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember {
        val apiService = RetrofitClient.sonarApiService
        val database = AppDatabase.getDatabase(context)
        val dao = database.claimHistoryDao()
        PerplexityRepository(apiService, dao)
    }

    // Collect history from the repository as a StateFlow
    val historyList by repository.getHistory().collectAsState(initial = emptyList())

    // Create the app UI using a Scaffold
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
        // Create a Box to display the appropriate screen based on the selected screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // When the selected screen is QUERY, display the QueryInputScreen
            when (selectedScreen) {
                Screen.QUERY -> QueryInputScreen(
                    // When the user submits a query, launch a coroutine to verify the query
                    onSubmit = { query ->
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                val result = repository.verifyQuery(query)
                                currentResult = result
                                // Save the result to the database
                                repository.saveQuery(result)
                                selectedScreen = Screen.RESULT
                                isLoading = false
                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.message}"
                                isLoading = false
                            }
                        }
                    },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
                // When the selected screen is HISTORY, display the HistoryScreen
                Screen.HISTORY -> HistoryScreen(
                    historyList = historyList,
                    // When the user clicks on a history item, launch a coroutine to display the result
                    onItemClick = { result ->
                        currentResult = result
                        selectedScreen = Screen.RESULT
                    },
                    // When the user deletes a history item, launch a coroutine to delete it from the database
                    onDelete = { id ->
                        scope.launch {
                            try {
                                repository.deleteQuery(id)
                            } catch (e: Exception) {
                                errorMessage = "Error deleting: ${e.message}"
                            }
                        }
                    }
                )
                // When the selected screen is RESULT, display the ResultDisplayScreen
                Screen.RESULT -> {
                    if (currentResult != null) {
                        ResultDisplayScreen(
                            result = currentResult!!,
                            // When the user clicks on the "New Query" button, launch a coroutine to display the QueryInputScreen again
                            onNewQuery = {
                                currentResult = null
                                errorMessage = null
                                selectedScreen = Screen.QUERY
                            }
                        )
                    }
                }
            }
        }
    }
}

// Define a enum class for the different screens of the app
enum class Screen {
    QUERY,
    HISTORY,
    RESULT
}
