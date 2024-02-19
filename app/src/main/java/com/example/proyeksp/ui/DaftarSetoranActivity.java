package com.example.proyeksp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyeksp.R;

public class DaftarSetoranActivity extends AppCompatActivity {
    TextView tvNoData;
    RecyclerView rvRekening;
    RekeningAdapter rekAdapter;
    RekeningViewModel rekViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_setoran);

        tvNoData = findViewById(R.id.tv_no_data);
        rvRekening = findViewById(R.id.rek_recycler);
        rvRekening.setLayoutManager(new LinearLayoutManager(this));

        rekViewModel = new ViewModelProvider(this).get(RekeningViewModel.class);
        rekViewModel.getDaftarRekening().observe(this, rekenings -> {
            rekAdapter = new RekeningAdapter(this, rekenings);
            rvRekening.setAdapter(rekAdapter);
            if (rekenings.size() == 0) {
                tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}