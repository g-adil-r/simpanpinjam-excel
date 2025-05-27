package com.example.proyeksp.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.repository.RekeningRepo
import kotlinx.coroutines.launch

class RekeningViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = RekeningRepo(application)
    val _allRekening = MutableLiveData<List<Rekening>>()
    val foundRekening = MutableLiveData<Rekening>()
//    val allRekening: LiveData<List<Rekening>> = mRepository.rekeningList

    val success: LiveData<Boolean>
        get() = mRepository.getSuccess()

    fun update(rekening: Rekening) {
        mRepository.update(rekening)
    }

    fun getRekeningByNoRek(s: String): Rekening? {
        return mRepository.findRekeningByNoRek(s)
    }

    fun exportToXls(uri: Uri) {
        mRepository.exportToXls(uri)
    }

    fun importFromXlsx(uri: Uri) {
        try {
            mRepository.importFromXlsx(uri)
        } catch (e: RuntimeException) {
            throw RuntimeException(e)
        }
    }

//    val daftarRekening: LiveData<List<Rekening>>?
//        get() = mRepository.daftarRekening
    val scanData: LiveData<Int>?
        get() = mRepository.scanData
    val totalSetoran: LiveData<Long?>?
        get() = mRepository.totalSetoran

    // ------------------------------------------------------

    fun fetchAllRekening() {
        viewModelScope.launch {
            _allRekening.value = mRepository.getAllRekening()
            Log.d("RekeningViewModel", "Fetched ${_allRekening.value?.size} records")
        }
    }

    fun getRekeningFromNoRek(s: String) {
        viewModelScope.launch {
            foundRekening.value = mRepository.getRekeningByNoRek(s)
            Log.d("RekeningViewModel", "Fetched ${_allRekening.value?.size} records")
        }
    }
}
