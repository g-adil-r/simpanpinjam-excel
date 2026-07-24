package com.example.proyeksp.ui

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import com.example.proyeksp.R

class LoginActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by lazy { AuthViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { role ->
                    val destination = if (role == "admin") {
                        MainAdminActivity::class.java
                    } else {
                        MainActivity::class.java
                    }
                    startActivity(Intent(this, destination))
                    finish()
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val ctx = LocalContext.current

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Error -> {
                Toast.makeText(ctx, (uiState as AuthUiState.Error).message, Toast.LENGTH_SHORT).show()
            }
            is AuthUiState.SuccessAdmin -> onLoginSuccess("admin")
            is AuthUiState.SuccessPetugas -> onLoginSuccess("petugas")
            else -> Unit
        }
    }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds
            ),
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_bumdes),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            Spacer(Modifier.height(60.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Login Simpan Pinjam",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is AuthUiState.Loading
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = uiState !is AuthUiState.Loading
                    )

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = { authViewModel.login(username, password) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is AuthUiState.Loading && username.isNotBlank() && password.isNotBlank()
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Masuk")
                        }
                    }
                }
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.footer, null)
            }
        )
    }
}

//@Preview
//@Composable
//private fun LoginScreenPreview() {
//    LoginScreen(
//        authViewModel = AuthViewModel(Application()),
//        onLoginSuccess = {}
//    )
//}