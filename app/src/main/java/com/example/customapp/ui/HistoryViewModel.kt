package com.example.customapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customapp.data.PerplexityRepository
import com.example.customapp.data.model.VerificationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: PerplexityRepository) : ViewModel() {

    val historyFlow: StateFlow<List<VerificationResult>> = repository.getHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    fun deleteQuery(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteQuery(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
