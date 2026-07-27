package com.example.proyeksp.ui

import android.util.Log
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

class ScanViewModel(private val mRepository: RekeningRepo = RekeningRepo()) : ViewModel() {
    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun getRekeningFromNoRek(s: String) {
        if (_uiState.value == ScanUiState.Loading) return

        _uiState.value = ScanUiState.Loading

        viewModelScope.launch {
            mRepository.getRekeningFromNoRek(s)
                .onSuccess { rekening ->
                    _uiState.value = ScanUiState.Success(rekening)
                }
                .onFailure { e ->
                    val message = if (e is NoSuchElementException)
                        "Nomor rekening $s tidak ditemukan"
                    else (e.message?: "Unknown error")
                    _uiState.value = ScanUiState.Error(message)
                }
        }
    }

    fun resetUiState() {
        _uiState.value = ScanUiState.Idle
    }
}