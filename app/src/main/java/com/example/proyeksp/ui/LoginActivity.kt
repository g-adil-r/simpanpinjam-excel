package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.proyeksp.R
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import kotlin.toString

class LoginActivity : AppCompatActivity() {
    val btLogin: Button by lazy { findViewById(R.id.bt_login) }
    val etUser: TextView by lazy { findViewById(R.id.et_user) }
    val etPass: TextView by lazy { findViewById(R.id.et_password) }
    private var authViewModel: AuthViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        authViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[AuthViewModel::class.java]

        observeSession()
        observeUiState()

        btLogin.setOnClickListener {
            val email = etUser.text.toString()
            val password = etPass.text.toString()

            authViewModel!!.login(email, password)
        }
    }

    private fun observeSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel!!.sessionStatus.collect { status ->
                    when (status) {
                        is SessionStatus.Authenticated -> {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    // TODO: add progress bar or spinner
    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel!!.uiState.collect { state ->
                    when (state) {
                        is AuthUiState.Loading -> {
                            // showProgressBar(true)
                        }
                        is AuthUiState.Error -> {
                            // showProgressBar(false)
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        is AuthUiState.Success -> {
                            // The sessionStatus observer will handle navigation
                            Toast.makeText(this@LoginActivity, "Selamat datang", Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}