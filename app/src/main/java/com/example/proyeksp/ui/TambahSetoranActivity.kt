package com.example.proyeksp.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.proyeksp.R
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.helper.CurrencyHelper
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class TambahSetoranActivity : AppCompatActivity(), View.OnClickListener {
    var rekViewModel: RekeningViewModel? = null
    var tvNoRek: TextView? = null
    var tvNama: TextView? = null
    var tvSimpanan: TextView? = null
    var tvPinjaman: TextView? = null
    var tvAngsuran: TextView? = null
    var etSetoran: EditText? = null
    var btSimpan: Button? = null
    var rekening: Rekening? = null
    var nf: NumberFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_setoran)
        rekViewModel = ViewModelProvider(this).get(
            RekeningViewModel::class.java
        )
        nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("ID"))

        val noRek = intent.getStringExtra("noRek")
        rekening = rekViewModel!!.getRekeningByNoRek(noRek)

        tvNoRek = findViewById(R.id.tv_no_rek)
        tvNama = findViewById(R.id.tv_nama)
        tvSimpanan = findViewById(R.id.tv_simpanan)
        tvPinjaman = findViewById(R.id.tv_pinjaman)
        tvAngsuran = findViewById(R.id.tv_angsuran)
        etSetoran = findViewById(R.id.et_setoran)
        btSimpan = findViewById(R.id.bt_simpan)

        tvNoRek.setText(rekening!!.noRek)
        tvNama.setText(rekening.getNama())
        tvSimpanan.setText(CurrencyHelper.format(rekening.getSaldoSimpanan()))
        tvPinjaman.setText(CurrencyHelper.format(rekening.getSaldoPinjaman()))
        tvAngsuran.setText(CurrencyHelper.format(rekening.getAngsuran()))

        btSimpan.setOnClickListener(this)
        etSetoran.setText(nf.format(rekening.getSetoran()))
        etSetoran.addTextChangedListener(currencyTextWatcher())
    }

    override fun onClick(v: View) {
        if (v.id == btSimpan!!.id) {
            if (etSetoran!!.text.toString() == "") {
                etSetoran!!.error = "Harap isi nilai setoran"
                return
            }

            val setoran = etSetoran!!.text.toString().replace(".", "").toLong()

            if (rekening.getSetoran() > 0) {
                showEditAlert(setoran)
            } else {
                editSetoran(setoran)
            }
        }
    }

    private fun editSetoran(setoran: Long) {
        rekening.setTglTrans(System.currentTimeMillis())
        rekening.setSetoran(setoran)
        rekViewModel!!.update(rekening)

        Toast.makeText(this, "Setoran berhasil disimpan", Toast.LENGTH_SHORT).show()
        val i = Intent(this, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
    }

    private fun currencyTextWatcher(): TextWatcher {
        return object : TextWatcher {
            private var previousText = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                previousText = s.toString()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                etSetoran!!.removeTextChangedListener(this)

                val originalString = s.toString()
                val cleanedString = originalString.replace(".", "")

                // Parse the cleanedString back to int and format it with decimal separator
                try {
                    val parsedInt = cleanedString.toInt()
                    val formattedString = nf!!.format(parsedInt.toLong())

                    // Calculate the cursor position
                    val lengthDiff = formattedString.length - originalString.length
                    var newCursorPos = etSetoran!!.selectionStart + lengthDiff
                    newCursorPos = max(newCursorPos.toDouble(), 0.0).toInt()

                    // If the new string is the same as the old one (except for the cursor position), restore the old string
                    if (newCursorPos == 1 && formattedString == previousText) {
                        etSetoran!!.setText(previousText)
                        etSetoran!!.setSelection(etSetoran!!.text.length)
                    } else {
                        etSetoran!!.setText(formattedString)
                        etSetoran!!.setSelection(
                            min(
                                newCursorPos.toDouble(),
                                formattedString.length.toDouble()
                            ).toInt()
                        )
                    }
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                etSetoran!!.addTextChangedListener(this)
            }
        }
    }

    fun showEditAlert(setoran: Long) {
        val message = this.getString(
            R.string.alert_dialog,
            rekening.getNama(),
            CurrencyHelper.format(rekening.getSetoran())
        )

        val alert = AlertDialog.Builder(this)
            .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton(
                "Simpan"
            ) { dialogInterface: DialogInterface?, i: Int -> editSetoran(setoran) }
            .setNegativeButton(
                "Batal"
            ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }
            .setCancelable(true)
            .create()

        alert.setOnShowListener { dialogInterface: DialogInterface? ->
            alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(Color.DKGRAY)
        }

        alert.show()
    }
}