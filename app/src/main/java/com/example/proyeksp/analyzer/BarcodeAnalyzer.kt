package com.example.proyeksp.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

// Changed the callback type to a Kotlin function type (String?) -> Unit
// Or (String) -> Unit if you ensure to only pass non-null values
class BarcodeAnalyzer(private val onBarcodeDetected: (String) -> Unit) : // Expects non-null String
    ImageAnalysis.Analyzer {

    private val scanner: BarcodeScanner

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        scanner = BarcodeScanning.getClient(options)
        // No need to assign to another 'onBarcodeScanned' field, just use the constructor parameter 'onBarcodeDetected'
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close() // Make sure to close imageProxy in all paths
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        // You already initialized 'scanner' in init, no need to re-initialize here
        // val options = BarcodeScannerOptions.Builder() ...
        // val barcodeScanner = BarcodeScanning.getClient(options)

        scanner.process(image) // Use the member 'scanner'
            .addOnSuccessListener { barcodes: List<Barcode> ->
                if (barcodes.isNotEmpty()) {
                    val displayValue = barcodes[0].displayValue
                    if (displayValue != null) { // ML Kit's displayValue can be null
                        onBarcodeDetected(displayValue) // Call the Kotlin lambda directly
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure, e.g., log it
                // Log.e("BarcodeAnalyzer", "Barcode scanning failed", e)
            }
            .addOnCompleteListener {
                // It's important to close the imageProxy whether success or failure
                imageProxy.close()
            }
        // Note: scanner.close() was removed from addOnSuccessListener.
        // If you close it here, you can only scan one barcode per analyzer instance.
        // If you want continuous scanning, don't close it. If it's for a single scan,
        // ensure it's closed when the analyzer is no longer needed or after a successful scan
        // if that's the desired behavior (like navigating away).
    }
}

//package com.example.proyeksp.analyzer
//
//import androidx.annotation.OptIn
//import androidx.camera.core.ExperimentalGetImage
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import com.google.android.gms.tasks.Task
//import com.google.mlkit.vision.barcode.BarcodeScanner
//import com.google.mlkit.vision.barcode.BarcodeScannerOptions
//import com.google.mlkit.vision.barcode.BarcodeScanning
//import com.google.mlkit.vision.barcode.common.Barcode
//import com.google.mlkit.vision.common.InputImage
//import java.util.function.Consumer
//
//class BarcodeAnalyzer(onBarcodeScanned: (String) -> Unit) :
//    ImageAnalysis.Analyzer {
//    private val scanner: BarcodeScanner
//    private val onBarcodeScanned: Consumer<String?>
//
//    init {
//        val options = BarcodeScannerOptions.Builder()
//            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
//            .build()
//        scanner = BarcodeScanning.getClient(options)
//
//        this.onBarcodeScanned = onBarcodeScanned
//    }
//
//    @OptIn(ExperimentalGetImage::class)
//    override fun analyze(imageProxy: ImageProxy) {
//        if (imageProxy.image == null) return
//
//        val image = InputImage.fromMediaImage(
//            imageProxy.image!!,
//            imageProxy.imageInfo.rotationDegrees
//        )
//
//        val options = BarcodeScannerOptions.Builder()
//            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
//            .build()
//
//        val barcodeScanner = BarcodeScanning.getClient(options)
//
//        barcodeScanner.process(image)
//            .addOnSuccessListener { barcodes: List<Barcode> ->
//                scanner.close()
//                if (barcodes.size > 0) {
//                    onBarcodeScanned.accept(barcodes[0].displayValue)
//                }
//            }
//            .addOnCompleteListener { task: Task<List<Barcode?>?>? -> imageProxy.close() }
//    }
//}
