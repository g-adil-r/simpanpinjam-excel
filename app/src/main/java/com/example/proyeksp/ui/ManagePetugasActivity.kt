package com.example.proyeksp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import com.example.proyeksp.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyeksp.database.Petugas
import com.example.proyeksp.ui.theme.MyTypography
import kotlin.jvm.java

class ManagePetugasActivity : ComponentActivity() {
    private val viewModel: PetugasViewModel by lazy { PetugasViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        enableEdgeToEdge()
        setContent {
            // Use your app theme here
//            MaterialTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                ) {
                    ManagePetugasScreen(viewModel)
//                }
//            }
        }
    }
}

//@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ManagePetugasScreen(petugasViewModel: PetugasViewModel = viewModel()) {
    val petugasList by petugasViewModel.petugasList.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kelola Petugas",
                    style = MyTypography.textTitle
                )
                Button(
                    onClick = {
                        ctx.startActivity(Intent(ctx, PetugasFormActivity::class.java))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Petugas"
                    )
                    Text("Tambah Petugas")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(petugasList) { petugas ->
                    PetugasItem(petugas)
                    Spacer(modifier = Modifier.height(8.dp))
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
fun PetugasItem(petugas: Petugas) {
    var expanded by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    // 1. Animate the rotation angle (0 degrees when collapsed, 180 degrees when expanded)
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ChevronRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(), // 2. Smoothly animates height changes during expansion
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 3. Wrap Title and Chevron in a Row to push the arrow to the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = petugas.namaLengkap.toString(),
                    style = MyTypography.textTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp,
                    modifier = Modifier.weight(1f) // Prevents long names from overlapping the icon
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Sembunyikan Detail" else "Tampilkan Detail",
                    modifier = Modifier.rotate(rotationState) // Applies the rotating transition
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (expanded) {
                Column (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoRow("Username", petugas.username.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("No. KTP", petugas.noKtp.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("Alamat", petugas.alamat.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("No. Telp", petugas.noTelp.toString())
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow("Role", petugas.role.toString())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(ctx, PetugasFormActivity::class.java)
                                intent.putExtra("petugas", petugas)
                                ctx.startActivity(intent)
                            }
                        ) {
                            Text("Edit")
                        }
                        Button(
                            onClick = { /* Handle delete action */ }
                        ) {
                            Text("Nonaktifkan")
                        }
                    }
                }
            } else {
                Text(
                    text = petugas.username.toString(),
                    style = MyTypography.textNormal,
                    fontSize = 17.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = petugas.role.toString(),
                    style = MyTypography.textBold,
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MyTypography.textNormal,
            modifier = Modifier
                .weight(4f)
        )

        Text(
            text = ":",
            style = MyTypography.textNormal,
            modifier = Modifier.weight(0.1f)
        )

        Text(
            text = value,
            style = MyTypography.textNormal,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
//    val petugasList = listOf(
//        Petugas(1, "Budi Santoso", "budi123", "08123456789", "1234567890123456", "Jl.123", "Admin"),
//        Petugas(2, "Siti Aminah", "sssiti", "08123456789", "1234567890123456", "Jl.1245","Petugas Lapangan"),
//        Petugas(3, "Agus Hermawan", "AgusH", "08123456789", "1234567890123456", "Jl.Aapap", "Bendahara")
//    )
    ManagePetugasScreen()
}