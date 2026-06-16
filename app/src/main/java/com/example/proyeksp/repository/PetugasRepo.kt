package com.example.proyeksp.repository

import android.util.Log
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.database.SupabaseService
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

object PetugasRepo {
    private val supabase = SupabaseService.client
    private val funcName = "sp-admin"

    private val _petugasList = MutableStateFlow<List<Petugas>>(emptyList())
    val petugasList: StateFlow<List<Petugas>> = _petugasList.asStateFlow()


    suspend fun getAllPetugas(): List<Petugas> {
        val columns = Columns.raw("""
                    id,
                    nama_lengkap,
                    username,
                    no_telp,
                    no_ktp,
                    alamat,
                    role
                """.trimIndent())

        return try {
            val data = supabase
                .from("petugas")
                .select(columns = columns)
                .decodeList<Petugas>()
            _petugasList.value = data
            data
        } catch (e: Exception) {
            Log.d("PetugasRepo", "Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun addPetugas(petugas: Petugas, password: String): Result<Unit> {
        val payload = CreatePetugasPayload(petugas, password)
        val requestBody = RequestBody("create", payload)
        try {
            val response = supabase.functions.invoke(
                function = funcName,
                body = requestBody,
                headers = Headers.build {
                    append(HttpHeaders.ContentType, "application/json")
                }
            )
            return if (response.status.value == 200) {
                _petugasList.update { it + petugas }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to add petugas"))
            }
        } catch (e: BadRequestRestException) {
            return if (e.error == "user_already_exists") Result.failure(Exception("Username sudah digunakan"))
            else Result.failure(e)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

//    suspend fun editPetugas(petugas: Petugas, password: String): Result<Unit> {
//        val payload = EditPetugasPayload(petugas, password)
//        val requestBody = RequestBody("update", payload)
//    }
    @Serializable
    private data class RequestBody<T>(
        val action: String,
        val payload: T,
    )

    @Serializable
    private data class CreatePetugasPayload(
        val petugas: Petugas,
        val password: String,
    )
}