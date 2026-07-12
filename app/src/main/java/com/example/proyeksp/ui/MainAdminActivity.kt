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
import com.example.proyeksp.ui.theme.AppColors
import com.example.proyeksp.ui.theme.MainButton
import com.example.proyeksp.ui.theme.MyTypography

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAdminScreen(rekViewModel: RekeningViewModel = viewModel()) {
    val context = LocalContext.current
    val uiSuccessState by rekViewModel.uiState.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            rekViewModel.exportToXls(uri)
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
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MainButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Lavender,
                    ),
                    onClick = {
                        context.startActivity(Intent(context, ManagePetugasActivity::class.java))
                    }
                ) {
                    Text(text = stringResource(id = R.string.kelola_petugas), style = MyTypography.textButton)
                }

                MainButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Green,
                    ),
                    onClick = {
                        context.startActivity(Intent(context, ManagePetugasActivity::class.java))
                    }
                ) {
                    Text(text = stringResource(id = R.string.lihat_data), style = MyTypography.textButton)
                }

                MainButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Pink,
                    ),
                    onClick = {
                        exportLauncher.launch(null)
                    }
                ) {
                    Text(text = stringResource(id = R.string.export_data), style = MyTypography.textButton)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
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