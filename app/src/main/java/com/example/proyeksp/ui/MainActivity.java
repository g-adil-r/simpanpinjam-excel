package com.example.proyeksp.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyeksp.R;
import com.example.proyeksp.helper.CurrencyHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btImport, btScan, btViewData, btExport;
    TextView tvScanCount, tvTotalSetoran;
    RekeningViewModel rekViewModel;
    private static final int PERMISSION_CODE = 1001;
    ActivityResultLauncher<Intent> exportCSVLauncher, importCSVLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);

        rekViewModel = new ViewModelProvider(this).get(RekeningViewModel.class);

        btImport = findViewById(R.id.bt_import);
        btScan = findViewById(R.id.bt_scan);
        btViewData = findViewById(R.id.bt_view_data);
        btExport = findViewById(R.id.bt_export);

        tvScanCount = findViewById(R.id.tv_scan_count);
        tvTotalSetoran = findViewById(R.id.tv_total_setoran);

        btImport.setOnClickListener(this);
        btScan.setOnClickListener(this);
        btViewData.setOnClickListener(this);
        btExport.setOnClickListener(this);

        rekViewModel.getScanData().observe(this, scanCount -> tvScanCount.setText(scanCount.toString()));
        rekViewModel.getTotalSetoran().observe(this, total -> {
                try {
                    tvTotalSetoran.setText(CurrencyHelper.format(total));
                } catch (RuntimeException e) {
                    if (total == null) {
                        tvTotalSetoran.setText("Rp0");
                    } else {
                        Toast.makeText(this, "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        tvTotalSetoran.setText("-");
                    }
                }
            }
        );

        exportCSVLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                res -> {
                    if (res.getResultCode() == Activity.RESULT_OK) {
                        Intent data = res.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            rekViewModel.exportToXls(uri);
                            rekViewModel.getSuccess().observe(this, success -> {
                                if (success)
                                    Toast.makeText(this, "Berhasil export data", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(this, "Gagal export data", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
        );

        importCSVLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                res -> {
                    if (res.getResultCode() == Activity.RESULT_OK) {
                        Intent data = res.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            rekViewModel.importFromXlsx(uri);
                            rekViewModel.getSuccess().observe(this, success -> {
                                if (success)
                                    Toast.makeText(this, "Berhasil import data", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(this, "Gagal import data", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btImport.getId()) {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"};
            i.setType("*/*");
            i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            importCSVLauncher.launch(i);
        } else if (v.getId() == btScan.getId()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, PERMISSION_CODE);
            } else {
                startActivity(new Intent(this, ScanActivity.class));
            }
        } else if (v.getId() == btViewData.getId()) {
            startActivity(new Intent(this, DaftarSetoranActivity.class));
        } else if (v.getId() == btExport.getId()) {
            exportCSVLauncher.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Harap izinkan aplikasi untuk menggunakan kamera", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (requestCode == PERMISSION_CODE) {
            startActivity(new Intent(this, ScanActivity.class));
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}