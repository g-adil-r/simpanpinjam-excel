package com.example.proyeksp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.proyeksp.R
import com.example.proyeksp.helper.CurrencyHelper

class MainActivity : AppCompatActivity(), View.OnClickListener {
    val btImport: Button by lazy { findViewById(R.id.bt_import) }
    val btScan: Button by lazy { findViewById(R.id.bt_scan) }
    val btViewData: Button by lazy { findViewById(R.id.bt_view_data) }
    val btExport: Button by lazy { findViewById(R.id.bt_export) }
    val tvScanCount: TextView by lazy { findViewById(R.id.tv_scan_count) }
    val tvTotalSetoran: TextView by lazy { findViewById(R.id.tv_total_setoran) }
    var rekViewModel: RekeningViewModel? = null
    var exportCSVLauncher: ActivityResultLauncher<Intent>? = null
    var importCSVLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen: SplashScreen = installSplashScreen()
        setContentView(R.layout.activity_main)

        rekViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[RekeningViewModel::class.java]

//        btImport = findViewById(R.id.bt_import)
//        btScan = findViewById(R.id.bt_scan)
//        btViewData = findViewById(R.id.bt_view_data)
//        btExport = findViewById(R.id.bt_export)

//        tvScanCount = findViewById(R.id.tv_scan_count)
//        tvTotalSetoran = findViewById(R.id.tv_total_setoran)

        btImport.setOnClickListener(this)
        btScan.setOnClickListener(this)
        btViewData.setOnClickListener(this)
        btExport.setOnClickListener(this)

        rekViewModel!!.getScanData()
        rekViewModel!!.scanNum.observe(
            this
        ) { scanCount: Int? -> tvScanCount.setText(scanCount.toString()) }
        rekViewModel!!.totalSetoran?.observe(
            this
        ) { total: Long? ->
            try {
                tvTotalSetoran.setText(total?.let { CurrencyHelper.format(it) })
            } catch (e: RuntimeException) {
                if (total == null) {
                    tvTotalSetoran.setText("Rp0")
                } else {
                    Toast.makeText(
                        this,
                        "Error: " + e.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    tvTotalSetoran.setText("-")
                }
            }
        }

        exportCSVLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { res: ActivityResult ->
            if (res.resultCode == RESULT_OK) {
                val data = res.data
                if (data != null) {
                    val uri = data.data
                    if (uri != null) {
                        rekViewModel!!.exportToXls(uri)
                    }
                    rekViewModel?.success?.observe(
                        this
                    ) { success: Boolean? ->
                        if (success!!) Toast.makeText(
                            this,
                            "Berhasil export data",
                            Toast.LENGTH_SHORT
                        ).show()
                        else Toast.makeText(
                            this,
                            "Gagal export data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        importCSVLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { res: ActivityResult ->
            if (res.resultCode == RESULT_OK) {
                val data = res.data
                if (data != null) {
                    val uri = data.data
                    if (uri != null) {
                        rekViewModel!!.importFromXlsx(uri)
                    }
                    rekViewModel?.success?.observe(
                        this
                    ) { success: Boolean? ->
                        if (success!!) Toast.makeText(
                            this,
                            "Berhasil import data",
                            Toast.LENGTH_SHORT
                        ).show()
                        else Toast.makeText(
                            this,
                            "Gagal import data",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == btImport.id) {
            val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            importCSVLauncher!!.launch(i)
        } else if (v.id == btScan.id) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_CODE
                )
            } else {
                startActivity(Intent(this, ScanActivity::class.java))
            }
        } else if (v.id == btViewData.id) {
            startActivity(Intent(this, DaftarSetoranActivity::class.java))
        } else if (v.id == btExport.id) {
            exportCSVLauncher!!.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        for (r in grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this,
                    "Harap izinkan aplikasi untuk menggunakan kamera",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        if (requestCode == PERMISSION_CODE) {
            startActivity(Intent(this, ScanActivity::class.java))
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val PERMISSION_CODE = 1001
    }
}