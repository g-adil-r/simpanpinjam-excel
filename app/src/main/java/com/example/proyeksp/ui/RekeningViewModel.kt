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

class RekeningViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = RekeningRepo(application)
    val foundRekening = MutableLiveData<Rekening>()
    val scanNum = MutableLiveData<Int>()
    private val _allSetoran = MutableLiveData<List<Transaksi>>()
    val allSetoran: LiveData<List<Transaksi>> = _allSetoran
    val rekeningWithTodaySetoran: StateFlow<List<Rekening>> = mRepository.rekeningWithTodaySetoran

    val success = MutableLiveData<Boolean?>()

    init {
        fetchTransaksi()
    }

    fun resetSuccessState() {
        // Resetting to null prevents duplicate/stale Toasts from appearing
        success.value = null
    }

//    fun update(rekening: Rekening) {
//        mRepository.update(rekening)
//    }

//    fun getRekeningByNoRek(s: String): Rekening? {
//        return mRepository.findRekeningByNoRek(s)
//    }

    fun exportToXls(uri: Uri) {
        viewModelScope.launch {
            mRepository.exportToXls(uri)
        }
    }

//    fun importFromXlsx(uri: Uri) {
//        try {
//            mRepository.importFromXlsx(uri)
//        } catch (e: RuntimeException) {
//            throw RuntimeException(e)
//        }
//    }

//    val daftarRekening: LiveData<List<Rekening>>?
//        get() = mRepository.daftarRekening
//    val scanData: LiveData<Int>?
//        get() = mRepository.scanData
//    val totalSetoran: LiveData<Long?>?
//        get() = mRepository.totalSetoran

    // -------------------------------------------------------------------------------------

//    fun fetchAllRekening() {
//        viewModelScope.launch {
//            _allRekening.value = mRepository.getAllRekening()
//            Log.d("RekeningViewModel", "Fetched ${_allRekening.value?.size} records")
//        }
//    }

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
        }
    }
}
