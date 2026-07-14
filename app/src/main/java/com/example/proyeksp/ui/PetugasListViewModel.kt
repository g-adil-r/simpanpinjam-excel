package com.example.proyeksp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.repository.PetugasRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ListState {
    object Idle : ListState()
    object Loading : ListState()
    data class Success(val data: List<Petugas>, val message: String?) : ListState()
    data class Error(val message: String) : ListState()
}

class PetugasListViewModel : ViewModel() {
    val petugasList : StateFlow<List<Petugas>> = PetugasRepo.petugasList

    private val _uiState = MutableStateFlow<ListState>(ListState.Idle)
    val uiState: StateFlow<ListState> = _uiState

    init {
        viewModelScope.launch {
            getAllPetugas()
        }
    }

    fun getAllPetugas() {
        viewModelScope.launch {
            _uiState.value = ListState.Loading
            val result = PetugasRepo.getAllPetugas()
            if (result.isSuccess) {
                _uiState.value = ListState.Success(petugasList.value, null)
            } else {
                _uiState.value = ListState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun deactivatePetugas(petugas: Petugas) {
        viewModelScope.launch {
            _uiState.value = ListState.Loading
            val result = PetugasRepo.deactivatePetugas(petugas)
            if (result.isSuccess) {
                PetugasRepo.getAllPetugas()
                _uiState.value = ListState.Success(petugasList.value, "Deaktivasi berhasil")
            } else {
                _uiState.value = ListState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = ListState.Idle
    }
}