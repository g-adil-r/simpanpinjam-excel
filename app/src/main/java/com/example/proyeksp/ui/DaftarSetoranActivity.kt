package com.example.proyeksp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyeksp.R
import com.example.proyeksp.helper.CurrencyHelper
import com.example.proyeksp.helper.DateHelper
import com.example.proyeksp.ui.theme.AppTypography

class DaftarSetoranActivity : ComponentActivity() {
    private val viewModel: RekeningViewModel by lazy { RekeningViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            DaftarSetoranScreen(viewModel)
        }
    }
}

@Composable
fun DaftarSetoranScreen(viewModel: RekeningViewModel = viewModel()) {
    val rekeningList by viewModel.rekeningWithTodaySetoran.collectAsStateWithLifecycle()

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
            Text(
                text = stringResource(id = R.string.daftar_title),
                fontSize = 24.sp,
                style = AppTypography.textTitle,
                softWrap = true,
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 15.dp)
            )
            if (rekeningList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.no_data), style = AppTypography.textNormal)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(rekeningList) { rek ->
                        val setoran = rek.setoran?.getOrNull(0)
                        val anggota = rek.anggota
                        val tanggalStr = DateHelper.formatInstant(setoran?.tglTrans)
                        SetoranItem(
                            nama = anggota?.nama ?: "",
                            noRek = rek.noRek,
                            tglTransaksi = tanggalStr,
                            setoran = setoran?.setoran
                        )
                    }
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

@Composable
fun SetoranItem(
    nama: String,
    noRek: String,
    tglTransaksi: String,
    setoran: Long?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(0.55f)
            ) {
                Text(
                    text = nama,
                    fontSize = 24.sp,
                    style = AppTypography.textTitle,
                    softWrap = true
                )
                Text(
                    text = noRek,
                    style = AppTypography.textNormal,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (tglTransaksi!= "-") {
                    Text(
                        text = tglTransaksi,
                        style = AppTypography.textNormal,
                        modifier = Modifier.padding(top = 4.dp),
                        color = Color(red = 34, green = 177, blue = 76)
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.no_date_setor),
                        style = AppTypography.textNormal,
                        modifier = Modifier.padding(top = 4.dp),
                        color = Color.Red
                    )
                }

            }

            Spacer(modifier = Modifier.weight(0.05f))

            if (setoran != null) {
                val setoranStr = CurrencyHelper.format(setoran)
                Text(
                    text = setoranStr,
                    style = AppTypography.textTitle,
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.End
                )
            } else {
                Text(
                    text = "-",
                    style = AppTypography.textTitle,
                    modifier = Modifier.weight(0.4f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetoranItemPreview() {
    MaterialTheme {
        SetoranItem(
            nama = "John Doe",
            noRek = "1234567890",
            tglTransaksi = "08 Jul 2026",
            setoran = 150000
        )
    }
}