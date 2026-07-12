package com.example.proyeksp.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.database.Transaksi
import com.example.proyeksp.repository.RekeningRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    object Success : ExportState()
    data class Error(val message: String) : ExportState()
}

class RekeningViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = RekeningRepo(application)
    val foundRekening = MutableLiveData<Rekening>()
    val scanNum = MutableLiveData<Int>()
    private val _allSetoran = MutableLiveData<List<Transaksi>>()
    val allSetoran: LiveData<List<Transaksi>> = _allSetoran
    val rekeningWithTodaySetoran: StateFlow<List<Rekening>> = mRepository.rekeningWithTodaySetoran

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val uiState: StateFlow<ExportState> = _exportState

    init {
        fetchTransaksi()
    }

    fun getRekeningFromNoRek(s: String) {
        viewModelScope.launch {
            foundRekening.value = mRepository.getRekeningFromNoRek(s)
            Log.d("RekeningViewModel", "Found rekening: ${foundRekening.value}")
        }
    }

    fun updateRekening(rekening: Rekening) {
        viewModelScope.launch {
            mRepository.updateRekening(rekening)
        }
    }

    fun addSetoran(transaksi: Transaksi) {
        viewModelScope.launch {
            mRepository.addSetoran(transaksi)
        }
    }

    fun getScanData() {
        viewModelScope.launch {
            scanNum.value = mRepository.getNumberOfScan()
        }
    }

    fun fetchTransaksi() {
        viewModelScope.launch {
            mRepository.getRekeningWithTodaySetoran()
            Log.d("RekeningViewModel", "Fetched ${rekeningWithTodaySetoran.value.size} records")
        }
    }

    fun exportToXls(uri: Uri) {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            val result = mRepository.exportToXls(uri)
            if (result.isSuccess) {
                _exportState.value = ExportState.Success
            } else {
                _exportState.value = ExportState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetSuccessState() {
        _exportState.value = ExportState.Idle
    }
}
