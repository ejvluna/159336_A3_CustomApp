package com.example.customapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.data.model.VerificationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QueryViewModel(private val repository: PerplexityRepository) : ViewModel() {

    sealed class UiState {
        data object Idle : UiState()
        data object Loading : UiState()
        data class Success(val result: VerificationResult) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun verifyQuery(query: String) {
        if (query.isBlank()) {
            _uiState.value = UiState.Error("Query cannot be empty")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = repository.verifyQuery(query)
                _uiState.value = UiState.Success(result)
                repository.saveQuery(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
