package com.example.proyeksp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.proyeksp.R

class MainAdminActivity : AppCompatActivity() {
    private val btManage: Button by lazy { findViewById(R.id.bt_manage) }
    private val btViewData: Button by lazy { findViewById(R.id.bt_view_data) }
    private val btReport: Button by lazy { findViewById(R.id.bt_report) }

    private lateinit var exportCSVLauncher: ActivityResultLauncher<Intent>

    private lateinit var rekViewModel: RekeningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main_admin)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // 1. Initialize the Launcher (Register this during onCreate)
        exportCSVLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { res: ActivityResult ->
            Log.d("MainAdminActivity", "Result code: ${res.resultCode}")
            Log.d("MainAdminActivity", "Is okay?: ${res.resultCode == RESULT_OK}")
            Log.d("MainAdminActivity", "Result data: ${res.data}")
            Log.d("MainAdminActivity", "Result data data: ${res.data?.data}")
            if (res.resultCode == RESULT_OK) {
                res.data?.data?.let { uri ->
                    rekViewModel.exportToXls(uri)
                }
            } else {
                Toast.makeText(this, "Gagal export data", Toast.LENGTH_SHORT).show()
            }
        }

        rekViewModel = ViewModelProvider(this)[RekeningViewModel::class.java]

        // 2. Observe LiveData ONCE (Do NOT place this inside the launcher callback!)
        rekViewModel.success.observe(this) { success ->
            // Safely handle nullable Boolean without success!!
            when (success) {
                true -> {
                    Toast.makeText(this, "Berhasil export data", Toast.LENGTH_SHORT).show()
                    // ⚠️ Reset the LiveData state to null so the Toast doesn't pop up again
                    // when you rotate the screen or perform a second export.
                    rekViewModel.resetSuccessState()
                }
                false -> {
                    Toast.makeText(this, "Gagal export data", Toast.LENGTH_SHORT).show()
                    rekViewModel.resetSuccessState()
                }
                null -> {
                    // Do nothing when state is idle/reset
                }
            }
        }

        btManage.setOnClickListener {
            startActivity(Intent(this, ManagePetugasActivity::class.java))
        }

        btViewData.setOnClickListener {
            startActivity(Intent(this, DaftarSetoranActivity::class.java))
        }

        btReport.setOnClickListener {
            exportCSVLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
        }
    }
}