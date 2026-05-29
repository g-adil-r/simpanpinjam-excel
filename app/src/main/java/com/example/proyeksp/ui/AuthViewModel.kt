package com.example.proyeksp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = AuthRepo()
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val sessionStatus : StateFlow<SessionStatus> = mRepository.sessionStatus

    // TODO: Remove this since this is for testing only
    init {
        viewModelScope.launch {
            mRepository.logout()
        }
    }

    fun login(emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = mRepository.login(emailInput, passwordInput)

            result.onSuccess {
                val user = mRepository.getCurrentPetugas()
                if (user != null) {
                    if (user.isAdmin()) {
                        _uiState.value = AuthUiState.SuccessAdmin
                    } else {
                        _uiState.value = AuthUiState.SuccessPetugas
                    }
                } else {
                    _uiState.value = AuthUiState.Error(
                        message = "An unknown error occurred"
                    )
                }
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(
                    message = exception.localizedMessage ?: "An unknown error occurred"
                )
            }
        }
    }
}