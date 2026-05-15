package com.example.proyeksp.repository

import com.example.proyeksp.database.SupabaseService
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow

class AuthRepo {
    private val supabase = SupabaseService.client
    val sessionStatus: StateFlow<SessionStatus> = supabase.auth.sessionStatus

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit) // Success!
        } catch (e: Exception) {
            Result.failure(e)    // Failed! Pass the exception back.
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
}