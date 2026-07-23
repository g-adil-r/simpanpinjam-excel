package com.example.proyeksp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyeksp.R
import com.example.proyeksp.ui.components.MainButton
import com.example.proyeksp.ui.theme.AppColors

class MainAdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainAdminScreen()
            }
        }
    }
}

@Composable
fun MainAdminScreen(rekViewModel: RekeningViewModel = viewModel()) {
    val context = LocalContext.current
    val uiSuccessState by rekViewModel.uiState.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            rekViewModel.exportToXls(uri, context)
        } else {
            Toast.makeText(context, "Batal export data", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiSuccessState) {
        if (uiSuccessState is ExportState.Success) {
            Toast.makeText(context, "Berhasil export data", Toast.LENGTH_SHORT).show()
            rekViewModel.resetSuccessState()
        } else if (uiSuccessState is ExportState.Error) {
            Toast.makeText(context, (uiSuccessState as ExportState.Error).message, Toast.LENGTH_SHORT).show()
            rekViewModel.resetSuccessState()
        }
    }

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
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MainButton(
                    text = stringResource(id = R.string.kelola_petugas),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Lavender,
                    ),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Kelola Petugas",
                            tint = Color.White,
                        )
                    },
                    onClick = {
                        context.startActivity(Intent(context, ManagePetugasActivity::class.java))
                    }
                )
                MainButton(
                    text = stringResource(id = R.string.lihat_data),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Green,
                    ),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                            contentDescription = "Lihat Data",
                            tint = Color.White,
                        )
                    },
                    onClick = {
                        context.startActivity(Intent(context, DaftarSetoranActivity::class.java))
                    }
                )
                MainButton(
                    text = stringResource(id = R.string.export_data),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Pink,
                    ),
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_docs_24),
                            contentDescription = "Lihat Data",
                            tint = Color.White,
                        )
                    },
                    onClick = {
                        exportLauncher.launch(null)
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    LayoutInflater.from(context).inflate(R.layout.footer, null)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAdminScreenPreview() {
    MaterialTheme {
        MainAdminScreen()
    }
}