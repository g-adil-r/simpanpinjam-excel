package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyeksp.R
import com.example.proyeksp.analyzer.QrCodeAnalyzer
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.ui.ScanUiState
import com.example.proyeksp.ui.ScanViewModel
import com.example.proyeksp.ui.TambahSetoranActivity
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

class ScanActivity : ComponentActivity() {
    private val viewModel by lazy { ScanViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanScreen(
                viewModel = viewModel,
                onNavigateToSetoran = { rekening ->
                    val intent = Intent(this, TambahSetoranActivity::class.java).apply {
                        val jsonString = Json.encodeToString(rekening)
                        putExtra("rekening", jsonString)
                    }
                    startActivity(intent)
                    viewModel.resetUiState()
                }
            )
        }
    }
}

@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    onNavigateToSetoran: (rekening: Rekening) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    // 1. Tangani Perpindahan Layar saat Rekening Ditemukan
    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success) {
            onNavigateToSetoran((uiState as ScanUiState.Success).rekening)
        } else if (uiState is ScanUiState.Error) {
            Toast.makeText(context, (uiState as ScanUiState.Error).message, Toast.LENGTH_SHORT).show()
            viewModel.resetUiState()
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
            Box(modifier = Modifier.fillMaxSize()) {
                // 3. Tampilkan Kamera PreviewView menggunakan AndroidView
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { previewView ->
                        val cameraProvider = ProcessCameraProvider.getInstance(context)
                        cameraProvider.addListener({
                            val cameraProvider = cameraProvider.get()

                            val preview = Preview.Builder().build().apply {
                                setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .apply {
                                    setAnalyzer(
                                        cameraExecutor,
                                        QrCodeAnalyzer(
                                            enabled = { uiState !is ScanUiState.Loading }
                                        ) { qrValue ->
                                            viewModel.getRekeningFromNoRek(qrValue)
                                        }
                                    )
                                }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                )

                // 4. Tampilkan Loader bulat transparan di tengah layar saat mencari ke database
                if (uiState is ScanUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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