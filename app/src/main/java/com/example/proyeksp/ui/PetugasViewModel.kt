package com.example.proyeksp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.repository.PetugasRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AdminState {
    object Idle : AdminState()
    object Loading : AdminState()
    object Success : AdminState()
    data class Error(val message: String) : AdminState()
}

class PetugasViewModel : ViewModel() {
    private val petugasRepo = PetugasRepo()
    val petugasList = MutableLiveData<List<Petugas>>()

    private val _uiState = MutableStateFlow<AdminState>(AdminState.Idle)
    val uiState: StateFlow<AdminState> = _uiState

    init {
        viewModelScope.launch {
            getAllPetugas()
        }
    }

    suspend fun getAllPetugas() {
        petugasList.value = petugasRepo.getAllPetugas()
    }

    fun addPetugas(petugas: Petugas, password: String) {
        viewModelScope.launch {
            _uiState.value = AdminState.Loading
            val result = petugasRepo.addPetugas(petugas, password)
            if (result.isSuccess) {
                _uiState.value = AdminState.Success
                getAllPetugas()
            } else {
                _uiState.value = AdminState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}