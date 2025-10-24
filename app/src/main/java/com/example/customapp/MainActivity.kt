// MainActivity.kt
/**
 * The main activity of the Verifica app.
 *
 * This activity serves as the entry point for the app, managing the app's lifecycle and
 * displaying the main UI. It coordinates between the CustomApp composable and the
 * PerplexityRepository for data access.
 */
package com.example.customapp

// Android Framework Imports for Activity lifecycle and state management, and asynchronous operations
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import kotlinx.coroutines.cancelChildren
// Jetpack Compose Imports for building UI structures, smooth transitions, and layout management
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
// Material Design 3 components & icons for consistent, modern UI
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
// Imports for data layer abstraction and management
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.data.api.RetrofitClient
import com.example.customapp.data.database.AppDatabase
import com.example.customapp.data.model.VerificationResult
// UI Layer Imports for screens and theme
import com.example.customapp.ui.HistoryScreen
import com.example.customapp.ui.QueryInputScreen
import com.example.customapp.ui.ResultDisplayScreen
import com.example.customapp.ui.theme.CustomAppTheme

// Main class that extends ComponentActivity to represent the main activity (entry point) of the app
class MainActivity : ComponentActivity() {
    // Lifecycle method called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call the CustomApp composable to display the app UI as the content of the activity
        setContent {
            CustomApp()
        }
    }
}
// Root composable that manages application state and screen navigation
@Composable
fun CustomApp() {
// STATE MANAGEMENT: Mutable state variables (current screen, result) that trigger UI recomposition when their values change
    var selectedScreen by remember { mutableStateOf(Screen.QUERY) }
    var currentResult by remember { mutableStateOf<VerificationResult?>(null) }

// DATA LAYER: Persistent repository with API service and database DAO, using context and coroutine scope for background operations
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember {
        val apiService = RetrofitClient.sonarApiService
        val database = AppDatabase.getDatabase(context)
        val dao = database.claimHistoryDao()
        PerplexityRepository(apiService, dao)
    }

// RESOURCE MANAGEMENT: Cleanup in-flight operations when composable leaves composition (e.g., activity destroyed or user navigates away)
    // Definition for the compose library function DisposableEffect to manage resources
    DisposableEffect(Unit) {
        // When the app closes, onDispose runs and cancels all pending coroutines (API calls, database operations, etc.)
        onDispose {
            scope.coroutineContext.cancelChildren()
        }
    }

// HELPER FUNCTIONS: Functions that perform specific tasks to manage app state and perform actions
    // Helper Function that handles the selection of a query from the history list
    fun onHistoryItemClick(item: VerificationResult) {
        // Store the selected item
        currentResult = item
        // Change the selected screen to the RESULT screen
        selectedScreen = Screen.RESULT
    }
    // Helper Function that navigates back to the Query screen
    fun onNewQuery() {
        selectedScreen = Screen.QUERY
    }

// UI COMPOSITION: Apply theme and build the main app layout with navigation using helper composable and functions
    CustomAppTheme {
        // Set modifiers to fill the max size of the screen and set the background color
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Call the BottomNavigationBar composable to display the bottom navigation bar
            Scaffold(
                bottomBar = { BottomNavigationBar(selectedScreen) { selectedScreen = it } }
            ) { padding ->
                // Display the appropriate screen container based on the selected tab by calling the appropriate helper composable
                Box(Modifier.padding(padding)) {
                    when (selectedScreen) {
                        Screen.QUERY -> QueryScreenContainer(
                            repository = repository,
                            onNavigateToResult = { result ->
                                currentResult = result
                                selectedScreen = Screen.RESULT
                            }
                        )
                        Screen.HISTORY -> HistoryScreenContainer(
                            repository = repository,
                            onItemClick = ::onHistoryItemClick
                        )
                        Screen.RESULT -> {
                            // Create a local, immutable copy of the state variable.
                            val result = currentResult
                            // Only show the Result screen if there IS a result. Otherwise, redirect back to the Query screen immediately.
                            if (result != null) {
                                ResultScreenContainer(
                                    currentResult = result,
                                    onNewQuery = ::onNewQuery
                                )
                            } else {
                                // If we land here with no result, go back to QUERY.
                                LaunchedEffect(Unit) {
                                    selectedScreen = Screen.QUERY
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Bottom navigation bar with tabs for switching between screens
@Composable
private fun BottomNavigationBar(
    selectedScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    // Material Design 3 NavigationBar component
    NavigationBar {
        // Navigation tab for submitting queries
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, "Search") },
            label = { Text("Verify") },
            selected = selectedScreen == Screen.QUERY,
            onClick = { onTabSelected(Screen.QUERY) }
        )
        // Navigation tab for viewing verification history
        NavigationBarItem(
            icon = { Icon(Icons.Filled.History, "History") },
            label = { Text("History") },
            selected = selectedScreen == Screen.HISTORY,
            onClick = { onTabSelected(Screen.HISTORY) }
        )
    }
}

// Composable container to display the Query screen with fade transition animation
@Composable
private fun QueryScreenContainer(
    repository: PerplexityRepository,
    onNavigateToResult: (VerificationResult) -> Unit
) {
    // Call ScreenTransition composable to wrap screen with fade transition animation
    ScreenTransition {
        // Call QueryInputScreen composable to render the QueryInputScreen UI component with ViewModel
        QueryInputScreen(
            onNavigateToResult = onNavigateToResult,
            repository = repository
        )
    }
}

// Composable container to display the Result screen with fade transition animation
@Composable
private fun ResultScreenContainer(
    currentResult: VerificationResult,
    onNewQuery: () -> Unit
) {
    // Call ScreenTransition composable to wrap screen with fade transition animation
    ScreenTransition {
        // Call ResultDisplayScreen composable to render the ResultsDisplayScreen UI component
        ResultDisplayScreen(
            result = currentResult,
            onNewQuery = onNewQuery
        )
    }
}

// Composable container to display the History screen with fade transition animation
@Composable
private fun HistoryScreenContainer(
    repository: PerplexityRepository,
    onItemClick: (VerificationResult) -> Unit
) {
    // Call ScreenTransition composable to wrap screen with fade transition animation
    ScreenTransition {
        // Call HistoryScreen composable to render the HistoryScreen UI component with ViewModel
        HistoryScreen(
            repository = repository,
            onItemClick = onItemClick
        )
    }
}

// Enum to define the three main screens in the app navigation
enum class Screen { QUERY, HISTORY, RESULT }

// Helper composable that centralizes animation logic for consistent fade transitions across all screens
@Composable
private fun ScreenTransition(content: @Composable () -> Unit) {
    // Use AnimatedVisibility to animate the content with fade in and fade out transitions
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        // Call the content composable to render the UI component
        content()
    }
}

