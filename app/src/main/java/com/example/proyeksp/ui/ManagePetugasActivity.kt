package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import com.example.proyeksp.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.ui.components.InfoRow
import com.example.proyeksp.ui.theme.AppColors
import com.example.proyeksp.ui.theme.AppTypography
import kotlin.jvm.java

class ManagePetugasActivity : ComponentActivity() {
    private val viewModel: PetugasListViewModel by lazy { PetugasListViewModel() }
    private val formLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("ManagePetugasActivity", "Form result OK")
            viewModel.getAllPetugas() // Refresh List
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ManagePetugasScreen(
                viewModel,
                onAddClick = {
                    val intent = Intent(this, PetugasFormActivity::class.java)
                    formLauncher.launch(intent)
                },
                onEditClick = { petugas ->
                    val intent = Intent(this, PetugasFormActivity::class.java).apply {
                        putExtra("petugas", petugas)
                    }
                    formLauncher.launch(intent)
                }
            )
        }
    }
}

@Composable
fun ManagePetugasScreen(
    viewModel: PetugasListViewModel = viewModel(),
    onAddClick: () -> Unit,
    onEditClick: (Petugas) -> Unit
) {
    val ctx = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            when (val state = uiState)  {
                is ListState.Idle -> {
                    Spacer(modifier = Modifier.weight(1f))
                }
                is ListState.Error -> {
                    Spacer(modifier = Modifier.weight(1f))
                    Toast.makeText(ctx, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetUiState()
                }
                is ListState.Loading -> {
                    Spacer(modifier = Modifier.weight(1f))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally),
                        color = AppColors.Lavender,
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                is ListState.Success -> {
                    if (state.message != null) Toast.makeText(ctx, state.message, Toast.LENGTH_SHORT).show()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kelola Petugas",
                            style = AppTypography.textTitle
                        )
                        Button(onClick = onAddClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Petugas"
                            )
                            Text("Tambah Petugas")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                    ) {
                        items(state.data) { petugas ->
                            PetugasItem(
                                petugas = petugas,
                                onEditClick = { onEditClick(petugas) },
                                onDeactivateClick = {
                                    viewModel.deactivatePetugas(petugas)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
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
}

@Composable
fun PetugasItem(petugas: Petugas, onEditClick: () -> Unit, onDeactivateClick: (Petugas) -> Unit, ) {
    var expanded by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }

    // 1. Animate the rotation angle (0 degrees when collapsed, 180 degrees when expanded)
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ChevronRotation"
    )

    if (showAlert) {
        val message = stringResource(R.string.petugas_alert_dialog, petugas.namaLengkap.toString())
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Deaktivasi Petugas") },
            text = {
                Text(
                    text = AnnotatedString.fromHtml(message)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeactivateClick(petugas)
                        showAlert = false
                    }
                ) {
                    Text(text = "Deaktivasi", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlert = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(), // 2. Smoothly animates height changes during expansion
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 3. Wrap Title and Chevron in a Row to push the arrow to the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = petugas.namaLengkap.toString(),
                    style = AppTypography.textTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp,
                    modifier = Modifier.weight(1f) // Prevents long names from overlapping the icon
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Sembunyikan Detail" else "Tampilkan Detail",
                    modifier = Modifier.rotate(rotationState) // Applies the rotating transition
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val roleStr = if (petugas.isAktif == false) "Tidak Aktif" else petugas.role.toString()
            if (expanded) {
                Column (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoRow("Username", petugas.username.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("No. KTP", petugas.noKtp.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("Alamat", petugas.alamat.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("No. Telp", petugas.noTelp.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("Role", roleStr)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onEditClick
                        ) {
                            Text("Edit")
                        }
                        Button(
                            onClick = { showAlert = true }
                        ) {
                            Text("Nonaktifkan")
                        }
                    }
                }
            } else {
                Text(
                    text = petugas.username.toString(),
                    style = AppTypography.textNormal,
                    fontSize = 17.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = roleStr,
                    style = AppTypography.textBold,
                    color = if (petugas.isAktif == false) Color.Red else Color.Black
                )
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
//    val petugasList = listOf(
//        Petugas(1, "Budi Santoso", "budi123", "08123456789", "1234567890123456", "Jl.123", "Admin"),
//        Petugas(2, "Siti Aminah", "sssiti", "08123456789", "1234567890123456", "Jl.1245","Petugas Lapangan"),
//        Petugas(3, "Agus Hermawan", "AgusH", "08123456789", "1234567890123456", "Jl.Aapap", "Bendahara")
//    )
//    ManagePetugasScreen()
}