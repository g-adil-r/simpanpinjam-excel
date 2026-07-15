package com.example.proyeksp.repository

import android.util.Log
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.database.SupabaseService
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

class AuthRepo {
    private val funcName = "sp-login"
    private val supabase = SupabaseService.client
    val sessionStatus: StateFlow<SessionStatus> = supabase.auth.sessionStatus

    suspend fun login(username: String, pass: String): Result<Unit> {
        return try {
            // 1. Invoke the Edge Function with the raw credentials
            val response = supabase.functions.invoke(
                function = funcName,
                body = RequestBody(username, pass),
                headers = Headers.build {
                    append(HttpHeaders.ContentType, "application/json")
                }
            )

            if (response.status.value == 200) {
                // 2. Deserialize the JSON directly into supabase-kt's UserSession object
                val session = response.body<UserSession>()

                // 3. Import the session into your local Supabase client
                supabase.auth.importSession(session) // Local persistence & auto-refresh starts here!

                Result.success(Unit)
            } else {
                Result.failure(Exception("Gagal masuk: Username atau password salah"))
            }
        } catch (e: UnauthorizedRestException) {
            Result.failure(Exception("Gagal masuk: Username atau password salah"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentPetugas(): Petugas? {
        val user = supabase.auth.currentUserOrNull()

        if (user == null) return null

        Log.d("AuthRepo", "ID: ${user.id}")

        val petugas = supabase
            .from("petugas")
            .select(
                Columns.list("id", "userid", "username", "role")
            ) {
                filter {
                    eq("userid", user.id)
                }
            }
            .decodeSingleOrNull<Petugas>()
        Log.d("AuthRepo", "Petugas: $petugas")
        return petugas
    }

    @Serializable
    private data class RequestBody(val username: String, val password: String)
}