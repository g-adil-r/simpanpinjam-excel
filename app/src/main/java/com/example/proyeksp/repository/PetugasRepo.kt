package com.example.proyeksp.repository

import android.util.Log
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.database.SupabaseService
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

object PetugasRepo {
    private val supabase = SupabaseService.client
    private val funcName = "sp-admin"

    private val _petugasList = MutableStateFlow<List<Petugas>>(emptyList())
    val petugasList: StateFlow<List<Petugas>> = _petugasList

    suspend fun getAllPetugas(): Result<List<Petugas>> {
        val columns = Columns.raw("""
                    id,
                    nama_lengkap,
                    username,
                    no_telp,
                    no_ktp,
                    alamat,
                    role,
                    is_aktif
                """.trimIndent())

        return withContext(Dispatchers.IO) {
            try {
                val data = supabase
                    .from("petugas")
                    .select(columns = columns)
                _petugasList.value = data.decodeList<Petugas>()
                Log.d("PetugasRepo", "Get all status: ${data.decodeList<JsonElement>()}")
                Result.success(_petugasList.value)
            } catch (e: Exception) {
                Log.d("PetugasRepo", "Error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun addPetugas(petugas: Petugas, password: String): Result<Unit> {
        val payload = PetugasPayload(petugas, password)
        val requestBody = RequestBody("create", payload)
        return withContext(Dispatchers.IO) {
            try {
                val response = supabase.functions.invoke(
                    function = funcName,
                    body = requestBody,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                    }
                )
                 if (response.status.value == 200) {
                     getAllPetugas()
                     Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to add petugas"))
                }
            } catch (e: BadRequestRestException) {
                if (e.error == "user_already_exists") Result.failure(Exception("Username sudah digunakan"))
                else Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun editPetugas(petugas: Petugas, password: String): Result<Unit> {
        val payload = PetugasPayload(petugas, password)
        val requestBody = RequestBody("update", payload)
        return withContext(Dispatchers.IO) {
            try {
                val response = supabase.functions.invoke(
                    function = funcName,
                    body = requestBody,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                    }
                )
                if (response.status.value == 200) {
                    getAllPetugas()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to add petugas"))
                }
            } catch (e: BadRequestRestException) {
                Log.d("PetugasRepo", "Error: ${e.message}")
                if (e.error == "user_already_exists") Result.failure(Exception("Username sudah digunakan"))
                else Result.failure(e)
            } catch (e: Exception) {
                Log.d("PetugasRepo", "Error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun deactivatePetugas(petugas: Petugas): Result<Unit> {
        val requestBody = RequestBody("deactivate", petugas.id)
        return withContext(Dispatchers.IO) {
            try {
                Log.d("PetugasRepo", "Deactivating petugas with ID: ${petugas.id}")
                val response = supabase.functions.invoke(
                    function = funcName,
                    body = requestBody,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                    }
                )
                Log.d("PetugasRepo", "Response status: ${response.status.value}")
                Log.d("PetugasRepo", "Response body: ${response.bodyAsText()}")
                if (response.status.value == 200) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to deactivate petugas"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @Serializable
    private data class RequestBody<T>(
        val action: String,
        val payload: T,
    )

    @Serializable
    private data class PetugasPayload(
        val petugas: Petugas,
        val password: String,
    )
}