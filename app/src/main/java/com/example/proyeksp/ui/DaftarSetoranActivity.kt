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
    val tvNoData: TextView by lazy { findViewById(R.id.tv_no_data) }
    val rvRekening: RecyclerView by lazy { findViewById(R.id.rek_recycler) }
    var rekAdapter: RekeningAdapter? = null
    var rekViewModel: RekeningViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_setoran)

//        tvNoData = findViewById(R.id.tv_no_data)
//        rvRekening = findViewById(R.id.rek_recycler)
        rvRekening.setLayoutManager(LinearLayoutManager(this))

        rekViewModel = ViewModelProvider(this)[RekeningViewModel::class.java]
        rekViewModel!!.fetchAllRekening()
        rekViewModel!!._allRekening.observe(
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