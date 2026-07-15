package com.example.proyeksp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.repository.PetugasRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class NetworkState {
    object Idle : NetworkState()
    object Loading : NetworkState()
    object Success : NetworkState()
    data class Error(val message: String) : NetworkState()
}

data class PetugasFormUiState(
    val isEditMode: Boolean = false,
    val networkState: NetworkState = NetworkState.Idle,
)

class PetugasFormViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PetugasFormUiState>(PetugasFormUiState())
    val uiState: StateFlow<PetugasFormUiState> = _uiState

    fun setEditMode(isEditMode: Boolean) {
        _uiState.update {
            it.copy(
                isEditMode = isEditMode
            )
        }
    }

    fun addPetugas(petugas: Petugas, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(networkState = NetworkState.Loading) }
            val result = PetugasRepo.addPetugas(petugas, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(networkState = NetworkState.Success) }
            } else {
                _uiState.update { it.copy(networkState = NetworkState.Error(result.exceptionOrNull()?.message ?: "Unknown error")) }
            }
        }
    }

    fun editPetugas(petugas: Petugas, password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(networkState = NetworkState.Loading)
            }
            val result = PetugasRepo.editPetugas(petugas, password)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(networkState = NetworkState.Success)
                }
            } else {
                _uiState.update {
                    it.copy(networkState = NetworkState.Error(result.exceptionOrNull()?.message ?: "Unknown error"))
                }
            }
        }
    }

    fun submitForm(petugas: Petugas, password: String) {
        if (_uiState.value.isEditMode) {
            editPetugas(petugas, password)
        } else {
            addPetugas(petugas, password)
        }
    }
}