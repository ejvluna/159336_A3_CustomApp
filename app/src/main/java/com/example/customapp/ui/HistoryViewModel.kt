// ui/HistoryViewModel.kt

/**
 * ViewModel for the History screen that manages verification history data.
 *
 * This ViewModel:
 * - Fetches verification history as a Flow from Room database
 * - Handles loading states and errors
 * - Processes item click events
 *
 * The UI observes historyItems StateFlow for reactive updates. Data is automatically
 * refreshed when the database changes.
 *
 */

package com.example.customapp.ui

// Import required packages for ViewModel lifecycle management, reactive data streams, and coroutines
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.data.model.VerificationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel that manages the history screen state and provides reactive access to past verification queries
class HistoryViewModel(private val repository: PerplexityRepository) : ViewModel() {
    // Create and store a StateFlow of all past verifications that automatically updated when database changes
    val historyFlow: StateFlow<List<VerificationResult>> = repository.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Mutable state holder for delete errors; exposed as immutable StateFlow to the UI
    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    // Function that deletes a verification record from the database by ID
    fun deleteQuery(id: Int) {
        // Launch a coroutine to avoid blocking the UI
        viewModelScope.launch {
            try {
                // Clear any previous error
                _deleteError.value = null
                // Try to delete the query from the database
                repository.deleteQuery(id)
                // If any exception occurs, update error state with user-friendly message
            } catch (e: Exception) {
                _deleteError.value = "Failed to delete: ${e.message ?: "Unknown error"}"
            }
        }
    }

    // Function to clear delete error (used when user dismisses error message)
    fun clearDeleteError() {
        _deleteError.value = null
    }
}
