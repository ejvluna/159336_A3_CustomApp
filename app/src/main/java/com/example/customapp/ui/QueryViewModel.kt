// ui/QueryViewModel.kt

/**
 * ViewModel for managing the query input screen's state and business logic.
 *
 * This ViewModel handles:
 * - Managing the query input state
 * - Validating user input
 * - Coordinating verification requests with the repository
 * - Managing loading and error states
 * - Processing verification results
 *
 * The UI state is exposed as StateFlow for reactive updates.
 */

package com.example.customapp.ui

// Import packages for ViewModel lifecycle management, reactive state management with StateFlow, and coroutines
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.data.model.VerificationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel that manages the query verification workflow using a state machine pattern (Idle → Loading → Success/Error)
class QueryViewModel(private val repository: PerplexityRepository) : ViewModel() {

    // Class representing the four possible states of the verification UI: Idle, Loading, Success, Error
    abstract class UiState {
        // Idle state: initial state before any verification is requested or after results are dismissed
        object Idle : UiState()
        // Loading state: displayed while the API call is in progress (prevents duplicate submissions)
        object Loading : UiState()
        // Success state: contains the verification result from the API (rating, summary, citations)
        data class Success(val result: VerificationResult) : UiState()
        // Error state: contains the error message to display when verification fails
        data class Error(val message: String) : UiState()
    }

    // Mutable state holder for internal use; exposed as immutable StateFlow to the UI
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Function that verifies a claim by calling the API and saving the result to the database; updates UI state throughout the process
    fun verifyQuery(query: String) {
        // Validate input and show error if empty
        if (query.isBlank()) {
            _uiState.value = UiState.Error("Query cannot be empty")
            return
        }
        // Set UI to loading state before making the API call
        _uiState.value = UiState.Loading
        // Then launch a coroutine to verify the query
        viewModelScope.launch {
            // Try to call the API to verify the claim and save the result to the database
            try {
                val result = repository.verifyQuery(query)
                _uiState.value = UiState.Success(result)
                repository.saveQuery(result)
                // If any exceptions occur, update the UI with error message
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Function to reset the UI state back to Idle (used when dismissing results or starting a new verification)
    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
