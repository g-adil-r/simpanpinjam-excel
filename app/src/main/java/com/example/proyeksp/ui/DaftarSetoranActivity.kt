package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyeksp.R
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.database.Transaksi
import com.example.proyeksp.ui.theme.MyTypography

class DaftarSetoranActivity : ComponentActivity() {
    val tvNoData: TextView by lazy { findViewById(R.id.tv_no_data) }
    val rvRekening: RecyclerView by lazy { findViewById(R.id.rek_recycler) }
    var rekAdapter: RekeningAdapter? = null
    private val viewModel: RekeningViewModel by lazy { RekeningViewModel(application) }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_daftar_setoran)
//
////        tvNoData = findViewById(R.id.tv_no_data)
////        rvRekening = findViewById(R.id.rek_recycler)
//        rvRekening.setLayoutManager(LinearLayoutManager(this))
//
//        rekViewModel = ViewModelProvider(this)[RekeningViewModel::class.java]
////        rekViewModel!!.fetchAllRekening()
//        rekViewModel!!._allRekening.observe(
//            this
//        ) { rekenings: List<Rekening?>? ->
//            rekAdapter = RekeningAdapter(this, rekenings)
//            rvRekening.setAdapter(rekAdapter)
//            if (rekenings!!.isEmpty()) {
//                tvNoData.visibility = View.VISIBLE
//            }
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    //        enableEdgeToEdge()
        setContent {
            // Use your app theme here
    //            MaterialTheme {
    //                Surface(
    //                    modifier = Modifier.fillMaxSize(),
    //                ) {
            DaftarSetoranScreen(viewModel)
    //                }
    //            }
        }
    }
}

@Composable
fun DaftarSetoranScreen(viewModel: RekeningViewModel = viewModel()) {
    val transaksiList by viewModel.allSetoran.observeAsState(initial = emptyList())

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
                .padding(16.dp)
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
            if (transaksiList.isEmpty()) {
                // Replaces R.id.tv_no_data
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Tidak ada data rekening", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                // Replaces RecyclerView
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transaksiList) { transaksi ->
                        val rekening = transaksi.rekening
                        val anggota = rekening?.anggota
                        SetoranItem(
                            nama = anggota?.nama ?: "",
                            noRek = rekening?.noRek ?: "",
                            tglTransaksi = "",
                            setoran = transaksi.setoran.toString()
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
    setoran: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            // Replicates the CardView's margin (15dp horizontal, 5dp vertical)
            .padding(horizontal = 15.dp, vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Replicates the inner ConstraintLayout's margin/padding
                .padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Replicates the LinearLayout (vertical) with 55% width percent
            Column(
                modifier = Modifier
                    .weight(0.55f)
            ) {
                Text(
                    text = nama,
                    fontSize = 24.sp,
                    // Maps to @style/text.title (Change to your app's title style)
                    style = MyTypography.textTitle,
                    softWrap = true // singleLine = false
                )
                Text(
                    text = noRek,
                    // Maps to @style/text.normal
                    style = MyTypography.textNormal,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = tglTransaksi,
                    // Maps to @style/text.normal
                    style = MyTypography.textNormal,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Spacer to prevent text collision
            Spacer(modifier = Modifier.weight(0.05f))

            // Replicates the right-aligned setor_item_setoran TextView (takes remaining 40% width)
            Text(
                text = setoran,
                // Maps to @style/text.title
                style = MyTypography.textTitle,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(0.40f)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }
    }
}

// Quick Preview to see your UI inside Android Studio
@Preview(showBackground = true)
@Composable
fun SetoranItemPreview() {
    MaterialTheme {
        SetoranItem(
            nama = "John Doe",
            noRek = "1234567890",
            tglTransaksi = "08 Jul 2026",
            setoran = "Rp 150.000"
        )
    }
}