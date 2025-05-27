package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.proyeksp.R
import com.example.proyeksp.analyzer.BarcodeAnalyzer

class ScanActivity : AppCompatActivity() {
    val previewView: PreviewView by lazy { findViewById(R.id.preview_view) }
    val progressBar: ProgressBar by lazy { findViewById(R.id.scanProgressBar) }
    var rekViewModel: RekeningViewModel? = null
    var cameraController: LifecycleCameraController? = null
    var toastMessage: Toast? = null
    val analyzer = BarcodeAnalyzer { s: String ->
        this.onDetectBarcode(
            s
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
//        previewView = findViewById(R.id.preview_view)

        rekViewModel = ViewModelProvider(this)[RekeningViewModel::class.java]

        cameraController = LifecycleCameraController(this)
        cameraController!!.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraController!!.setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        cameraController!!.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this), analyzer)
        cameraController!!.bindToLifecycle(this)

        previewView.setController(cameraController)
        progressBar.visibility = ProgressBar.GONE
    }

    override fun onResume() {
        super.onResume()
        cameraController!!.bindToLifecycle(this)
    }

    private fun onDetectBarcode(s: String) {
        Log.d("ScanActivity", "Barcode detected: $s. Checking with ViewModel.")
        progressBar.visibility = ProgressBar.VISIBLE
        cameraController!!.clearImageAnalysisAnalyzer()
        rekViewModel!!.getRekeningFromNoRek(s)
        rekViewModel!!.foundRekening.observe(this) {
            Log.d("ScanActivity", "Rekening found: ${it?.noRek}. Navigating.")
            if (it != null) {
                cameraController!!.unbind()
                val i = Intent(this, TambahSetoranActivity::class.java)
                i.putExtra("noRek", s)
                startActivity(i)
            } else {
                if (toastMessage != null) toastMessage!!.cancel()
                val newToast = Toast.makeText(
                    this,
                    "Nomor rekening '$s' tidak ditemukan", Toast.LENGTH_SHORT
                )
                newToast.show()
                toastMessage = newToast
                cameraController!!.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this), analyzer)
            }
        }
//        val rekening = rekViewModel!!.getRekeningByNoRek(s)
//
//        if (rekening == null) {
//            if (toastMessage != null) toastMessage!!.cancel()
//            val newToast = Toast.makeText(
//                this,
//                "Nomor rekening '$s' tidak ditemukan", Toast.LENGTH_SHORT
//            )
//            newToast.show()
//            toastMessage = newToast
//        } else {
//            cameraController!!.unbind()
//            val i = Intent(this, TambahSetoranActivity::class.java)
//            i.putExtra("noRek", s)
//            startActivity(i)
//        }
    }
}

//package com.example.proyeksp.ui
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.ProgressBar // For loading indicator
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.CameraController
//import androidx.camera.view.LifecycleCameraController
//import androidx.camera.view.PreviewView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.example.proyeksp.R
//import com.example.proyeksp.analyzer.BarcodeAnalyzer
//import com.example.proyeksp.database.Rekening
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//class ScanActivity : AppCompatActivity() {
//    private val previewView: PreviewView by lazy { findViewById(R.id.preview_view) }
//    private val progressBar: ProgressBar by lazy { findViewById(R.id.scanProgressBar) } // Add to layout
//
//    private lateinit var rekViewModel: RekeningViewModel
//    private lateinit var cameraController: LifecycleCameraController
//    private var barcodeAnalyzer: BarcodeAnalyzer? = null
//    private var toastMessage: Toast? = null
//
//    private var isCheckingRekening = false // Flag to prevent multiple checks for the same scan
//    private var lastScannedNoRek: String? = null
//
//    companion object {
//        private const val CAMERA_PERMISSION_REQUEST_CODE = 101
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_scan) // Ensure this layout has a ProgressBar
//
//        rekViewModel = ViewModelProvider(this)[RekeningViewModel::class.java]
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE
//            )
//        }
//        setupObservers()
//    }
//
//    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
//        this, Manifest.permission.CAMERA
//    ) == PackageManager.PERMISSION_GRANTED
//
//    private fun startCamera() {
//        cameraController = LifecycleCameraController(this)
//        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//        // Enable both IMAGE_ANALYSIS for barcode scanning and PREVIEW (or VIDEO_CAPTURE) for displaying camera feed
//        cameraController.setEnabledUseCases(CameraController.IMAGE_ANALYSIS or CameraController.VIDEO_CAPTURE)
//
//
//        barcodeAnalyzer = BarcodeAnalyzer { noRek ->
//            // This callback is on the main thread thanks to ContextCompat.getMainExecutor below
//            if (!isCheckingRekening && (lastScannedNoRek != noRek || toastMessage == null || !toastMessage!!.view!!.isShown )) {
//                lastScannedNoRek = noRek
//                onBarcodeDetected(noRek)
//            }
//        }
//
//        cameraController.setImageAnalysisAnalyzer(
//            ContextCompat.getMainExecutor(this), // Ensures analyzer callbacks are on the main thread
//            barcodeAnalyzer!!
//        )
//
//        // Bind controller to PreviewView AFTER setting up the analyzer
//        previewView.controller = cameraController
//
//        // Bind to lifecycle last to start camera
//        cameraController.bindToLifecycle(this)
//        Log.d("ScanActivity", "Camera started and bound to lifecycle.")
//    }
//
//
//    private fun setupObservers() {
//        rekViewModel.foundRekening.observe(this, Observer { rekeningResult ->
//            // This observer is called after rekViewModel.getRekeningByNoRek(s) completes
//            if (!isCheckingRekening) return@Observer // Only process if we initiated the check
//
//            showLoading(false) // Hide loading indicator
//
//            val scannedNoRek = lastScannedNoRek ?: return@Observer // Should have a value
//
//            if (rekeningResult != null) {
//                // Rekening found, proceed to next activity
//                Log.d("ScanActivity", "Rekening found: ${rekeningResult.noRek}. Navigating.")
//                isCheckingRekening = false // Reset flag
//                // Stop camera analysis before navigating to prevent further scans
//                // cameraController.clearImageAnalysisAnalyzer() // Temporarily stop analysis
//                cameraController.unbind() // More robust: unbind camera
//
//                val i = Intent(this, TambahSetoranActivity::class.java)
//                i.putExtra("noRek", scannedNoRek)
//                startActivity(i)
//                // No finish() here, so user can press back to ScanActivity
//            } else {
//                // Rekening not found, show toast and allow scanning to continue
//                Log.d("ScanActivity", "Rekening not found for: $scannedNoRek. Allowing rescan.")
//                if (toastMessage != null) toastMessage?.cancel()
//                toastMessage = Toast.makeText(
//                    this,
//                    "Nomor rekening '$scannedNoRek' tidak ditemukan", Toast.LENGTH_SHORT
//                )
//                toastMessage?.show()
//                // Allow scanning to resume implicitly by not stopping the analyzer permanently
//                // And reset isCheckingRekening
//                isCheckingRekening = false
//                // Camera should still be bound if we didn't unbind it.
//                // If we did unbind, onResume will handle re-binding.
//                // For immediate rescan without leaving the screen, ensure camera remains active or restart it here.
//                // The simplest is to let onResume handle it if we navigate away and back.
//                // If staying on the screen, the analyzer will just continue with the next frame.
//            }
//        })
//    }
//
//
//    private fun onBarcodeDetected(noRek: String) {
//        Log.d("ScanActivity", "Barcode detected: $noRek. Checking with ViewModel.")
//        isCheckingRekening = true // Set flag before calling ViewModel
//        lastScannedNoRek = noRek // Store the currently scanned noRek
//        showLoading(true)
//        rekViewModel.getRekeningByNoRek(noRek) // ViewModel fetches from Supabase
//    }
//
//    private fun showLoading(isLoading: Boolean) {
//        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // If permissions are granted and camera wasn't started (e.g., after permission grant)
//        // or if returning to the activity and camera was unbound.
//        if (allPermissionsGranted()) {
//            // Reset flags to allow scanning
//            isCheckingRekening = false
//            lastScannedNoRek = null // Clear last scanned on resume to allow re-scanning the same code if needed
//            // If cameraController is already initialized, rebind. Otherwise, startCamera will init and bind.
//            if (::cameraController.isInitialized) {
//                Log.d("ScanActivity", "onResume - Rebinding camera to lifecycle.")
//                // Ensure analyzer is set again if it could have been cleared
//                if (barcodeAnalyzer != null) {
//                    cameraController.setImageAnalysisAnalyzer(
//                        ContextCompat.getMainExecutor(this),
//                        barcodeAnalyzer!!
//                    )
//                }
//                cameraController.bindToLifecycle(this)
//            } else {
//                startCamera()
//            }
//        }
//        Log.d("ScanActivity", "onResume finished.")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Unbind camera when the activity is paused to release resources
//        // and stop analysis if it's still running.
//        if (::cameraController.isInitialized) {
//            Log.d("ScanActivity", "onPause - Unbinding camera from lifecycle.")
//            cameraController.unbind()
//        }
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>, grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Toast.makeText(this, "Camera permission is required to scan QR codes.", Toast.LENGTH_LONG).show()
//                finish() // Close if permission denied
//            }
//        }
//    }
//}