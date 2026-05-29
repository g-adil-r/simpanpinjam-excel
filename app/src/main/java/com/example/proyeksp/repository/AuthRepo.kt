package com.example.proyeksp.repository

import android.util.Log
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.database.SupabaseService
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

@Serializable
data class UserRole(val role: String)

class AuthRepo {
    private val supabase = SupabaseService.client
    val sessionStatus: StateFlow<SessionStatus> = supabase.auth.sessionStatus

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
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

    fun getCurrentUserEmail(): String? {
        return supabase.auth.currentUserOrNull()?.email
    }

    suspend fun getCurrentPetugas(): Petugas? {
        val user = supabase.auth.currentUserOrNull()

        if (user == null) return null

        Log.d("AuthRepo", "ID: ${user.id}")

        val petugas = supabase
            .from("petugas")
            .select(
                Columns.list("userid", "username", "role")
            ) {
                filter {
                    eq("userid", user.id)
                }
            }
            .decodeSingleOrNull<Petugas>()
        Log.d("AuthRepo", "Petugas: $petugas")
        return petugas
    }
}