package com.example.proyeksp.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyeksp.R
import com.example.proyeksp.analyzer.QrCodeAnalyzer
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.database.Transaksi
import com.example.proyeksp.helper.CurrencyHelper
import com.example.proyeksp.ui.components.InfoRow
import com.example.proyeksp.ui.components.MainButton
import com.example.proyeksp.ui.theme.AppColors
import com.example.proyeksp.ui.theme.AppTypography
import kotlinx.serialization.json.Json
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class TambahSetoranActivity : ComponentActivity() {
    private val viewModel: TambahSetoranViewModel by lazy { TambahSetoranViewModel() }
    val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rekeningJson = intent.getStringExtra("rekening")
        Log.d("TambahSetoranActivity", "Received noRek: $rekeningJson")

        //parse jsonstring rekening to rekening
        val rekening = rekeningJson?.let { Json.decodeFromString<Rekening>(it) }
        Log.d("TambahSetoranActivity", "Rekening: $rekening")

        val onSubmit: (setoran: Long) -> Unit = { setoran ->
            val newTransaksi = Transaksi(
                noRek = rekening!!.noRek,
                setoran = setoran,
                petugasId = 1
            )
            viewModel.addSetoran(newTransaksi)
        }

        val onSuccess: () -> Unit = {
            Toast.makeText(this, "Setoran berhasil disimpan", Toast.LENGTH_SHORT).show()
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
            finish()
        }

        setContent {
            SetoranScreen(
                viewModel = viewModel,
                rekening = rekening!!,
                onSuccess = onSuccess
            )
        }
    }
}

@Composable
fun SetoranScreen(
    viewModel: TambahSetoranViewModel = viewModel(),
    rekening: Rekening,
    onSubmit: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val netState = uiState.networkState
    var setoran by remember { mutableStateOf(rekening.setoran ?: 0) }
    val ctx = LocalContext.current

    LaunchedEffect(netState) {
        if (netState is SetorNetworkState.Success) {
            onSuccess()
        }
        else if (netState is SetorNetworkState.Error) {
            Toast.makeText(ctx, netState.message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    LayoutInflater.from(context).inflate(R.layout.header, null)
                },
                update = { view ->
                    // Gambar tidak muncul kalau tidak diset manual
                    val imageView = view.findViewById<ImageView>(R.id.imageView)
                    imageView.setImageResource(R.drawable.logo_bumdes)
                }
            )
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp),
            ) {
                Text(
                    text = stringResource(R.string.rekening_title),
                    style = AppTypography.textTitle,
                    modifier = Modifier.padding(top = 20.dp, start = 15.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Card (
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.LightLavender
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp
                    ),
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(all = 15.dp)
                    ) {
                        val anggota = rekening.anggota
                        val angsuran = rekening.angsuran
                        val pinjamanAwal = rekening.pinjamanAwal

                        val angsuranStr = if (angsuran != null) CurrencyHelper.format(angsuran) else ""
                        val pinjamanAwalStr = if (pinjamanAwal != null) CurrencyHelper.format(pinjamanAwal) else ""

                        InfoRow(stringResource(R.string.info_no_rek), rekening.noRek)
                        InfoRow(stringResource(R.string.info_nama), anggota?.nama)
                        InfoRow(stringResource(R.string.info_angsuran), angsuranStr)
                        InfoRow(stringResource(R.string.info_pinjaman_awal), pinjamanAwalStr)
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = stringResource(R.string.setoran_label),
                    style = AppTypography.textTitle,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card (
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    Row (
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.currency),
                            style = AppTypography.textLargeBold,
                        )
                        TextField(
                            value = setoran.toString(),
                            onValueChange = { setoran = it.filter { it.isDigit() } },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                            ),
                            textStyle = AppTypography.textLargeBold,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Lavender,
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(R.string.simpan),
                        style = AppTypography.textButton,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    LayoutInflater.from(context).inflate(R.layout.footer, null)
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SetoranScreenPreview() {
    val dummyRek = Rekening("1234567890")
    SetoranScreen(rekening = dummyRek)
}