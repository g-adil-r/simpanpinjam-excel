package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyeksp.R

class MainAdminActivity : AppCompatActivity() {
    private val btManage: Button by lazy { findViewById(R.id.bt_manage) }
    private val btViewData: Button by lazy { findViewById(R.id.bt_view_data) }
    private val btReport: Button by lazy { findViewById(R.id.bt_report) }

    var exportCSVLauncher: ActivityResultLauncher<Intent>? = null

    var rekViewModel: RekeningViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main_admin)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

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

        btManage.setOnClickListener {
            startActivity(Intent(this, ManagePetugasActivity::class.java))
        }

        btViewData.setOnClickListener {
            startActivity(Intent(this, DaftarSetoranActivity::class.java))
        }

        btReport.setOnClickListener {
            val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            exportCSVLauncher!!.launch(i)
        }
    }
}