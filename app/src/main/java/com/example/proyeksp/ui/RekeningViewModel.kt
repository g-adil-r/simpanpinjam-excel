package com.example.proyeksp.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.repository.RekeningRepo

class RekeningViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository = RekeningRepo(application)
    val allRekening: LiveData<List<Rekening?>?>? = mRepository.rekeningList

    val success: LiveData<Boolean?>?
        get() = mRepository.success

    fun update(rekening: Rekening?) {
        mRepository.update(rekening)
    }

    fun getRekeningByNoRek(s: String?): Rekening? {
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

    val daftarRekening: LiveData<List<Rekening?>?>?
        get() = mRepository.daftarRekening
    val scanData: LiveData<Int?>?
        get() = mRepository.scanData
    val totalSetoran: LiveData<Long?>?
        get() = mRepository.totalSetoran
}
