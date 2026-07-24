package com.example.proyeksp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.database.Transaksi
import com.example.proyeksp.repository.AuthRepo
import com.example.proyeksp.repository.RekeningRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SetorNetworkState() {
    object Idle : SetorNetworkState()
    object Loading : SetorNetworkState()
    object Success : SetorNetworkState()
    data class Error(val message: String) : SetorNetworkState()
}

data class SetoranFormUiState(
    val rekening: Rekening? = null,
    val networkState: SetorNetworkState = SetorNetworkState.Idle,
)

class TambahSetoranViewModel(): ViewModel() {
    private val rekeningRepo: RekeningRepo = RekeningRepo()
    private val authRepo: AuthRepo = AuthRepo()

    private val _uiState = MutableStateFlow<SetoranFormUiState>(SetoranFormUiState())
    val uiState: StateFlow<SetoranFormUiState> = _uiState

    fun addSetoran(noRek: String, setoran: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(networkState = SetorNetworkState.Loading) }

            val currentPetugas = authRepo.getCurrentPetugas()
            Log.d("TambahSetoranViewModel", "Current Petugas: $currentPetugas")

            if (currentPetugas == null) {
                _uiState.update { it.copy(
                    networkState = SetorNetworkState.Error("Petugas tidak ditemukan")
                )}
                return@launch
            }

            val newTransaksi = Transaksi(
                noRek = noRek,
                setoran = setoran,
                petugasId = currentPetugas.id!!
            )

            rekeningRepo.addSetoran(newTransaksi)
                .onSuccess {
                    _uiState.update { it.copy(networkState = SetorNetworkState.Success)}
                }
                .onFailure { e ->
                    _uiState.update { it.copy(
                        networkState = SetorNetworkState.Error(e.message ?: "Unknown error")
                    )}
                }
        }
    }
}