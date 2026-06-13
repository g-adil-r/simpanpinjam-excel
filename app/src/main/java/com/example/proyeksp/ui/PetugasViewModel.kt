package com.example.proyeksp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.repository.PetugasRepo
import kotlinx.coroutines.launch

class PetugasViewModel : ViewModel() {
    private val petugasRepo = PetugasRepo()
    val petugasList = MutableLiveData<List<Petugas>>()

    init {
        viewModelScope.launch {
            getAllPetugas()
        }
    }

    suspend fun getAllPetugas() {
        petugasList.value = petugasRepo.getAllPetugas()
    }
}