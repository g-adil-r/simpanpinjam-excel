package com.example.proyeksp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.IntentCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyeksp.R
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.ui.theme.MyTypography

enum class WorkerRole { PETUGAS, ADMIN }

class PetugasFormActivity : ComponentActivity() {
    private val viewModel: PetugasViewModel by lazy { PetugasViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        val petugas = IntentCompat.getParcelableExtra(intent, "petugas", Petugas::class.java)

        val onSaveClick: (
            nama: String, ktp: String, telp: String, alamat: String,
            user: String, pass: String, role: WorkerRole) -> Unit = {
            nama, ktp, telp, alamat, user, pass, role ->
                val roleString = when (role) {
                    WorkerRole.PETUGAS -> "petugas"
                    WorkerRole.ADMIN -> "admin"
                }
                val petugas = Petugas(
                    namaLengkap = nama,
                    noKtp = ktp,
                    noTelp = telp,
                    alamat = alamat,
                    username = user,
                    role = roleString
                )

                viewModel.addPetugas(petugas, pass)
        }

        val onCancelClick: () -> Unit = {
            finish()
        }

        setContent {
            PetugasFormScreen(petugas, onSaveClick, onCancelClick, viewModel)
        }
    }
}

@Composable
fun PetugasFormScreen(
    petugas: Petugas?,
    onSaveClick: (
        nama: String, ktp: String, telp: String, alamat: String,
        user: String, pass: String, role: WorkerRole
    ) -> Unit,
    onCancelClick: () -> Unit,
    viewModel: PetugasViewModel = viewModel(),
) {
    // Form States
    var namaLengkap by remember { mutableStateOf(petugas?.namaLengkap ?: "") }
    var nomorKtp by remember { mutableStateOf(petugas?.noKtp ?: "") }
    var nomorTelepon by remember { mutableStateOf(petugas?.noTelp ?: "") }
    var alamat by remember { mutableStateOf(petugas?.alamat ?: "") }
    var username by remember { mutableStateOf(petugas?.username ?: "") }

    var password by remember { mutableStateOf("") }

    var selectedRole by remember {
        mutableStateOf(
            if (petugas?.role?.lowercase() == "admin") WorkerRole.ADMIN else WorkerRole.PETUGAS
        )
    }

    var isPasswordVisible by remember { mutableStateOf(false) }

    var isUsernameValid = username.isNotEmpty()
    var isPasswordValid = password.isNotEmpty()

    var isFormValid = isUsernameValid && isPasswordValid

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState.value == AdminState.Loading
    val ctx = LocalContext.current

    LaunchedEffect(uiState.value) {
        if (uiState.value is AdminState.Success) {
            Toast.makeText(ctx, "Petugas berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            onCancelClick()
        }
        else if (uiState.value is AdminState.Error) {
            Toast.makeText(ctx, (uiState.value as AdminState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds
            ),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.header, null)
            },
            update = { view ->
                // Gambar tidak muncul kalau tidak diset manual
                val imageView = view.findViewById<ImageView>(R.id.imageView)
                imageView.setImageResource(R.drawable.logo_bumdes)
            }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (petugas == null) "Tambah Petugas" else "Edit Petugas",
                style = MyTypography.textTitle
            )

            OutlinedTextField(
                value = namaLengkap,
                onValueChange = { namaLengkap = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nomorKtp,
                onValueChange = { nomorKtp = it },
                label = { Text("Nomor KTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nomorTelepon,
                onValueChange = { nomorTelepon = it },
                label = { Text("Nomor Telepon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text("Alamat") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()
            Text("Kredensial Akun", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                isError = !isUsernameValid,
                supportingText = {
                    if (!isUsernameValid) {
                        Text("Username harus diisi")
                    }
                },
                label = { Text("Username*") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password*") },
                isError = !isPasswordValid,
                supportingText = {
                    if (!isPasswordValid) {
                        Text("Password harus diisi")
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()
            Text("Peran (Role)", style = MaterialTheme.typography.titleMedium)

            // Role selection with Radio Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedRole == WorkerRole.PETUGAS),
                        onClick = { selectedRole = WorkerRole.PETUGAS }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Petugas")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedRole == WorkerRole.ADMIN),
                        onClick = { selectedRole = WorkerRole.ADMIN }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Admin")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (isFormValid) {
                        onSaveClick(namaLengkap, nomorKtp, nomorTelepon, alamat, username, password, selectedRole)
                    } else {
                        Toast.makeText(ctx, "Harap lengkapi semua form wajib (*)", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Disable button while saving
            ) {
                if (isLoading) {
                    Text("Menyimpan...")
                } else {
                    Text(if (petugas == null) "Simpan Akun" else "Perbarui Akun")
                }
            }

            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Disable cancel while saving
            ) {
                Text("Batal")
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.footer, null)
            }
        )
    }
}

@Preview
@Composable
fun PetugasFormScreenPreview() {
    val dummyOnSave: (
        nama: String, ktp: String, telp: String, alamat: String,
        user: String, pass: String, role: WorkerRole) -> Unit = {
            nama, ktp, telp, alamat, user, pass, role -> {}
    }

    val dummyOnCancel: () -> Unit = {}

    val dummyData = Petugas(0,"Ghifari Adil", "username", "08123456789", "3507229191910001", "Jalan Puncak Indah", "petugas")

    PetugasFormScreen(dummyData, dummyOnSave, dummyOnCancel)
}