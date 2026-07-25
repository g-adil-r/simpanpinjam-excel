package com.example.proyeksp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyeksp.BuildConfig
import com.example.proyeksp.R
import com.example.proyeksp.helper.CurrencyHelper
import com.example.proyeksp.ui.components.MainButton
import com.example.proyeksp.ui.theme.AppColors
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onNavigateToScan: () -> Unit = {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }

        setContent {
            MainScreen(onNavigateToScan)
        }
    }
}

@Composable
fun MainScreen(onNavigateToScan: () -> Unit = {}) {
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted by user -> Navigate to ScanActivity
            onNavigateToScan()
        } else {
            Toast.makeText(
                context,
                "Harap izinkan aplikasi untuk menggunakan kamera",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onScanQrClicked() {
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (isCameraPermissionGranted) {
            onNavigateToScan()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
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
                    text = stringResource(id = R.string.scan_qr),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Pink,
                    ),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR",
                            tint = Color.White,
                        )
                    },
                    onClick = {
                        onScanQrClicked()
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
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen()
    }
}