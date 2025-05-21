package com.example.proyeksp.analyzer;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.function.Consumer;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    private final BarcodeScanner scanner;
    private final Consumer<String> onBarcodeScanned;

    public BarcodeAnalyzer(Consumer<String> onBarcodeScanned) {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        scanner = BarcodeScanning.getClient(options);

        this.onBarcodeScanned = onBarcodeScanned;
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) return;

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();

        BarcodeScanner barcodeScanner = BarcodeScanning.getClient(options);

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    scanner.close();
                    if (barcodes.size() > 0) {
                        onBarcodeScanned.accept(barcodes.get(0).getDisplayValue());
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }
}
