package com.example.proyeksp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyeksp.R;
import com.example.proyeksp.analyzer.BarcodeAnalyzer;
import com.example.proyeksp.database.Rekening;

public class ScanActivity extends AppCompatActivity {
    PreviewView previewView;
    RekeningViewModel rekViewModel;
    LifecycleCameraController cameraController;
    Toast toastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        previewView = findViewById(R.id.preview_view);

        rekViewModel = new ViewModelProvider(this).get(RekeningViewModel.class);

        BarcodeAnalyzer analyzer = new BarcodeAnalyzer(this::onDetectBarcode);

        cameraController = new LifecycleCameraController(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        cameraController.setEnabledUseCases(CameraController.IMAGE_ANALYSIS);
        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this), analyzer);
        cameraController.bindToLifecycle(this);

        previewView.setController(cameraController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraController.bindToLifecycle(this);
    }

    private void onDetectBarcode(String s) {
        Rekening rekening = rekViewModel.getRekeningByNoRek(s);

        if (rekening == null) {
            if (toastMessage != null) toastMessage.cancel();
            toastMessage = Toast.makeText(this, "Nomor rekening '"+s+"' tidak ditemukan", Toast.LENGTH_SHORT);
            toastMessage.show();
        } else {
            cameraController.unbind();
            Intent i = new Intent(this, TambahSetoranActivity.class);
            i.putExtra("noRek",s);
            startActivity(i);
        }
    }
}