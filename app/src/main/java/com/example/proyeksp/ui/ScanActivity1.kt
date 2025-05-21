package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
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
    var previewView: PreviewView? = null
    var rekViewModel: RekeningViewModel? = null
    var cameraController: LifecycleCameraController? = null
    var toastMessage: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        previewView = findViewById(R.id.preview_view)

        rekViewModel = ViewModelProvider(this).get(
            RekeningViewModel::class.java
        )

        val analyzer = BarcodeAnalyzer { s: String? ->
            this.onDetectBarcode(
                s
            )
        }

        cameraController = LifecycleCameraController(this)
        cameraController!!.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraController!!.setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        cameraController!!.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this), analyzer)
        cameraController!!.bindToLifecycle(this)

        previewView.setController(cameraController)
    }

    override fun onResume() {
        super.onResume()
        cameraController!!.bindToLifecycle(this)
    }

    private fun onDetectBarcode(s: String?) {
        val rekening = rekViewModel!!.getRekeningByNoRek(s)

        if (rekening == null) {
            if (toastMessage != null) toastMessage!!.cancel()
            toastMessage = Toast.makeText(
                this,
                "Nomor rekening '$s' tidak ditemukan", Toast.LENGTH_SHORT
            )
            toastMessage.show()
        } else {
            cameraController!!.unbind()
            val i = Intent(this, TambahSetoranActivity::class.java)
            i.putExtra("noRek", s)
            startActivity(i)
        }
    }
}