package com.example.proyeksp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyeksp.R
import com.example.proyeksp.database.Rekening

class DaftarSetoranActivity : AppCompatActivity() {
    var tvNoData: TextView? = null
    var rvRekening: RecyclerView? = null
    var rekAdapter: RekeningAdapter? = null
    var rekViewModel: RekeningViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_setoran)

        tvNoData = findViewById(R.id.tv_no_data)
        rvRekening = findViewById(R.id.rek_recycler)
        rvRekening.setLayoutManager(LinearLayoutManager(this))

        rekViewModel = ViewModelProvider(this).get(
            RekeningViewModel::class.java
        )
        rekViewModel.getDaftarRekening().observe(
            this
        ) { rekenings: List<Rekening?>? ->
            rekAdapter = RekeningAdapter(this, rekenings)
            rvRekening.setAdapter(rekAdapter)
            if (rekenings!!.size == 0) {
                tvNoData.setVisibility(View.VISIBLE)
            }
        }
    }
}