package com.example.proyeksp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyeksp.R;
import com.example.proyeksp.database.Rekening;
import com.example.proyeksp.helper.CurrencyHelper;

import java.text.NumberFormat;
import java.util.Locale;

public class TambahSetoranActivity extends AppCompatActivity implements View.OnClickListener {
    RekeningViewModel rekViewModel;
    TextView tvNoRek, tvNama, tvSimpanan, tvPinjaman, tvAngsuran;
    EditText etSetoran;
    Button btSimpan;
    Rekening rekening;
    NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_setoran);
        rekViewModel = new ViewModelProvider(this).get(RekeningViewModel.class);
        nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("ID"));

        String noRek = getIntent().getStringExtra("noRek");
        rekening = rekViewModel.getRekeningByNoRek(noRek);

        tvNoRek = findViewById(R.id.tv_no_rek);
        tvNama = findViewById(R.id.tv_nama);
        tvSimpanan = findViewById(R.id.tv_simpanan);
        tvPinjaman = findViewById(R.id.tv_pinjaman);
        tvAngsuran = findViewById(R.id.tv_angsuran);
        etSetoran = findViewById(R.id.et_setoran);
        btSimpan = findViewById(R.id.bt_simpan);

        tvNoRek.setText(rekening.getNoRek());
        tvNama.setText(rekening.getNama());
        tvSimpanan.setText(CurrencyHelper.format(rekening.getSaldoSimpanan()));
        tvPinjaman.setText(CurrencyHelper.format(rekening.getSaldoPinjaman()));
        tvAngsuran.setText(CurrencyHelper.format(rekening.getAngsuran()));

        btSimpan.setOnClickListener(this);
        etSetoran.setText(nf.format(rekening.getSetoran()));
        etSetoran.addTextChangedListener(currencyTextWatcher());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btSimpan.getId()) {
            if (etSetoran.getText().toString().equals("")) {
                etSetoran.setError("Harap isi nilai setoran");
                return;
            }

            long setoran = Long.parseLong(etSetoran.getText().toString().replace(".",""));

            if (rekening.getSetoran() > 0) {
                showEditAlert(setoran);
            } else {
                editSetoran(setoran);
            }
        }
    }

    private void editSetoran(Long setoran) {
        rekening.setTglTrans(System.currentTimeMillis());
        rekening.setSetoran(setoran);
        rekViewModel.update(rekening);

        Toast.makeText(this, "Setoran berhasil disimpan", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private TextWatcher currencyTextWatcher() {
        return new TextWatcher() {
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                etSetoran.removeTextChangedListener(this);

                String originalString = s.toString();
                String cleanedString = originalString.replace(".", "");

                // Parse the cleanedString back to int and format it with decimal separator
                try {
                    int parsedInt = Integer.parseInt(cleanedString);
                    String formattedString = nf.format(parsedInt);

                    // Calculate the cursor position
                    int lengthDiff = formattedString.length() - originalString.length();
                    int newCursorPos = etSetoran.getSelectionStart() + lengthDiff;
                    newCursorPos = Math.max(newCursorPos, 0);

                    // If the new string is the same as the old one (except for the cursor position), restore the old string
                    if (newCursorPos == 1 && formattedString.equals(previousText)) {
                        etSetoran.setText(previousText);
                        etSetoran.setSelection(etSetoran.getText().length());
                    } else {
                        etSetoran.setText(formattedString);
                        etSetoran.setSelection(Math.min(newCursorPos, formattedString.length()));
                    }
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                etSetoran.addTextChangedListener(this);
            }
        };
    }

    public void showEditAlert(Long setoran) {
        String message = this.getString(
                R.string.alert_dialog,
                rekening.getNama(),
                CurrencyHelper.format(rekening.getSetoran())
        );

        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("Simpan", (dialogInterface, i) -> editSetoran(setoran))
                .setNegativeButton("Batal", (dialogInterface, i) -> dialogInterface.cancel())
                .setCancelable(true)
                .create();

        alert.setOnShowListener(dialogInterface -> {
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
        });

        alert.show();
    }
}