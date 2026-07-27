package com.example.proyeksp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.repository.AuthRepo
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object SuccessAdmin : AuthUiState()
    object SuccessPetugas : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val mRepository: AuthRepo = AuthRepo()) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentPetugas = MutableLiveData<Petugas?>(null)

    init {
        viewModelScope.launch {
            mRepository.logout()
        }
        fetchProfile()
    }

    fun login(emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = mRepository.login(emailInput, passwordInput)

            result.onSuccess {
                mRepository.getCurrentPetugas()
                    .onSuccess { user ->
                        if (user != null) {
                            _currentPetugas.value = user
                            if (user.isAdmin()) {
                                _uiState.value = AuthUiState.SuccessAdmin
                            } else {
                                _uiState.value = AuthUiState.SuccessPetugas
                            }
                        } else {
                            _uiState.value = AuthUiState.Error(
                                message = "Data profil petugas tidak ditemukan."
                            )
                        }
                    }
                    .onFailure { e ->
                        _uiState.value = AuthUiState.Error(
                            message = e.localizedMessage ?: "Gagal memuat data profil petugas."
                        )
                    }
            }.onFailure { e ->
                _uiState.value = AuthUiState.Error(
                    message = e.localizedMessage ?: "An unknown error occurred"
                )
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            mRepository.getCurrentPetugas()
                .onSuccess { user ->
                    _currentPetugas.value = user
            }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(
                        message = e.localizedMessage ?: "Gagal memuat data profil petugas."
                    )
                }
        }
    }
}