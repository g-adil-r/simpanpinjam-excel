package com.example.proyeksp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.repository.RekeningRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScanUiState() {
    object Idle : ScanUiState()
    object Loading : ScanUiState()
    data class Success(val rekening: Rekening) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()
    private val mRepository = RekeningRepo()

    fun getRekeningFromNoRek(s: String) {
        if (_uiState.value == ScanUiState.Loading) return

        _uiState.value = ScanUiState.Loading

        viewModelScope.launch {
            mRepository.getRekeningFromNoRek(s)
                .onSuccess { rekening ->
                    _uiState.value = ScanUiState.Success(rekening)
                }
                .onFailure {
                    _uiState.value = ScanUiState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun resetUiState() {
        _uiState.value = ScanUiState.Idle
    }
}