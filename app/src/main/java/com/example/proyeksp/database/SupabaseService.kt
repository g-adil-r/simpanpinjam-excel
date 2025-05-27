package com.example.proyeksp.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseService {
    private const val SUPABASE_URL = "https://ijsfdaahvgqngjkwzgaq.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlqc2ZkYWFodmdxbmdqa3d6Z2FxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgzMjIzNzUsImV4cCI6MjA2Mzg5ODM3NX0.Aw9nctMKw_BmH2wHnD9ay9DEhOcqvXZzelq7vbZchOk"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            // install(GoTrue) // If you need authentication
        }
    }
}