package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.proyeksp.R
import com.example.proyeksp.ui.LoginActivity
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.getValue

class StartActivity : AppCompatActivity() {
    private var authViewModel: AuthViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_start)

        authViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AuthViewModel::class.java]

        lifecycleScope.launch {
            delay(1000)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel!!.sessionStatus.collect { status ->
                    when (status) {
                        is SessionStatus.Authenticated -> {
                            val intent = Intent(this@StartActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            val intent = Intent(this@StartActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }

//        Handler().postDelayed({
//            val i = Intent(this, LoginActivity::class.java)
//            startActivity(i)
//            finish()
//        }, 1000)
    }
}