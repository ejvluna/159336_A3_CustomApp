// ui/HistoryViewModel.kt

/**
 * ViewModel for managing the history screen's data and business logic.
 *
 * This ViewModel handles:
 * - Loading verification history from the repository
 * - Managing the list of past verifications
 * - Processing delete operations
 * - Maintaining UI state
 *
 * The data is exposed as StateFlow to enable reactive UI updates.
 */

package com.example.customapp.ui

// Import required packages for ViewModel lifecycle management, reactive data streams, and coroutines
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.data.model.VerificationResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel that manages the history screen state and provides reactive access to past verification queries
class HistoryViewModel(private val repository: PerplexityRepository) : ViewModel() {
    // Create and store a StateFlow of all past verifications that automatically updated when database changes
    val historyFlow: StateFlow<List<VerificationResult>> = repository.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    // Function that deletes a verification record from the database by ID
    fun deleteQuery(id: Int) {
        // Launch a coroutine to avoid blocking the UI
        viewModelScope.launch {
            // Use a try-catch block to handle exceptions
            try {
                // Try to delete the query from the database
                repository.deleteQuery(id)
                // If any exception occurs, print the stack trace for debugging
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
