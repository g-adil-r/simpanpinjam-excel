package com.example.proyeksp.repository

import android.util.Log
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.database.SupabaseService
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

class PetugasRepo {
    private val supabase = SupabaseService.client
    private val funcName = "sp-admin"

    suspend fun getAllPetugas(): List<Petugas> {
        Log.d("PetugasRepo", "Fetching records...")
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
                Result.success(Unit)
            } else {
                Log.d("PetugasRepo", "Error: ${response.status.value}")
                Result.failure(Exception("Failed to add petugas"))
            }
        } catch (e: Exception) {
            Log.d("PetugasRepo", "Error: ${e.message}")
            return Result.failure(e)
        }
    }

//    suspend fun editPetugas(petugas: Petugas, password: String): Result<Unit> {
//        val payload = EditPetugasPayload(petugas, password)
//        val requestBody = RequestBody("update", payload)
//    }
}

@Serializable
data class RequestBody<T>(
    val action: String,
    val payload: T,
)

@Serializable
data class CreatePetugasPayload(
    val petugas: Petugas,
    val password: String,
)