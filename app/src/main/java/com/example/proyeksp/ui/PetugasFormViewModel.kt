package com.example.proyeksp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.repository.PetugasRepo
import io.github.jan.supabase.exceptions.BadRequestRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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

class PetugasFormViewModel(
    private val petugasRepo: PetugasRepo = PetugasRepo
) : ViewModel() {
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
            val result = petugasRepo.addPetugas(petugas, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(networkState = NetworkState.Success) }
            } else {
                val e = result.exceptionOrNull()
                val message = if (e is BadRequestRestException) {
                    val errorObj = Json.decodeFromString<Map<String, String>>(e.error)
                    if (errorObj["error"] == "user_already_exists") "Username ${petugas.username} sudah digunakan"
                    else errorObj["message"] ?: "Unknown error"
                }
                else (e?.message ?: "Unknown error")
                Log.d("PetugasFormViewModel", "Error: ${(e as BadRequestRestException).error}")
                _uiState.update { it.copy(networkState = NetworkState.Error(message)) }
            }
        }
    }

    fun editPetugas(petugas: Petugas, password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(networkState = NetworkState.Loading)
            }
            val result = petugasRepo.editPetugas(petugas, password)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(networkState = NetworkState.Success)
                }
            } else {
                val e = result.exceptionOrNull()
                val message = if (e is BadRequestRestException) {
                    val errorObj = Json.decodeFromString<Map<String, String>>(e.error)
                    if (errorObj["error"] == "user_already_exists") "Username ${petugas.username} sudah digunakan"
                    else errorObj["message"] ?: "Unknown error"
                }
                else (e?.message ?: "Unknown error")
                Log.d("PetugasFormViewModel", "Error: ${(e as BadRequestRestException).error}")
                _uiState.update { it.copy(networkState = NetworkState.Error(message)) }
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