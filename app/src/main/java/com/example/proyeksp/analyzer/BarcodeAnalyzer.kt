package com.example.proyeksp.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.function.Consumer

class BarcodeAnalyzer(onBarcodeScanned: (String) -> Unit) :
    ImageAnalysis.Analyzer {
    private val scanner: BarcodeScanner
    private val onBarcodeScanned: Consumer<String?>

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        scanner = BarcodeScanning.getClient(options)

        this.onBarcodeScanned = onBarcodeScanned
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (imageProxy.image == null) return

        val image = InputImage.fromMediaImage(
            imageProxy.image!!,
            imageProxy.imageInfo.rotationDegrees
        )

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val barcodeScanner = BarcodeScanning.getClient(options)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes: List<Barcode> ->
                scanner.close()
                if (barcodes.size > 0) {
                    onBarcodeScanned.accept(barcodes[0].displayValue)
                }
            }
            .addOnCompleteListener { task: Task<List<Barcode?>?>? -> imageProxy.close() }
    }
}
