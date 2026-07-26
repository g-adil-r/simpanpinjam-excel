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
    val isEditMode: Boolean = false,
    val rekening: Rekening? = null,
    val networkState: SetorNetworkState = SetorNetworkState.Idle,
)

class TambahSetoranViewModel(): ViewModel() {
    private val rekeningRepo: RekeningRepo by lazy { RekeningRepo() }
    private val authRepo: AuthRepo by lazy { AuthRepo() }

    private val _uiState = MutableStateFlow<SetoranFormUiState>(SetoranFormUiState())
    val uiState: StateFlow<SetoranFormUiState> = _uiState

    fun setEditMode(isEditMode: Boolean) {
        _uiState.update {
            it.copy(
                isEditMode = isEditMode
            )
        }
    }

    fun addSetoran(noRek: String, setoran: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(networkState = SetorNetworkState.Loading) }
            authRepo.getCurrentPetugas()
                .onSuccess { currentPetugas ->
                    if (currentPetugas?.id == null) {
                        _uiState.update { it.copy(
                            networkState = SetorNetworkState.Error("Petugas tidak ditemukan. Silakan login ulang.")
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
                .onFailure { e ->
                    _uiState.update { it.copy(
                        networkState = SetorNetworkState.Error(e.message ?: "Gagal mengambil data petugas")
                    )}
                }
        }
    }

    fun editSetoran(setoranInit: Transaksi, noRek: String, setoran: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(networkState = SetorNetworkState.Loading) }
            authRepo.getCurrentPetugas()
                .onSuccess { currentPetugas ->
                    if (currentPetugas?.id == null) {
                        _uiState.update { it.copy(
                            networkState = SetorNetworkState.Error("Petugas tidak ditemukan. Silakan login ulang.")
                        )}
                        return@launch
                    }

                    val newTransaksi = Transaksi(
                        id = setoranInit.id,
                        noRek = noRek,
                        setoran = setoran,
                        petugasId = currentPetugas.id!!
                    )

                    Log.d("TambahSetoranViewModel", "New transaksi: $newTransaksi")

                    rekeningRepo.editSetoran(setoranInit.id!!, newTransaksi)
                        .onSuccess {
                            _uiState.update { it.copy(networkState = SetorNetworkState.Success)}
                        }
                        .onFailure { e ->
                            _uiState.update { it.copy(
                                networkState = SetorNetworkState.Error(e.message ?: "Unknown error")
                            )}
                        }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(
                        networkState = SetorNetworkState.Error(e.message ?: "Gagal mengambil data petugas")
                    )}
                }
        }
    }

    fun resetNetworkState() {
        _uiState.update { it.copy(networkState = SetorNetworkState.Idle) }
    }
}