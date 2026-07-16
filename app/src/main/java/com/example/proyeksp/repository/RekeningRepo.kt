package com.example.proyeksp.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.database.SupabaseService
import com.example.proyeksp.database.Transaksi
import com.example.proyeksp.helper.DateHelper
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonElement
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import java.io.IOException
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RekeningRepo(application: Application) {
    private val supabase = SupabaseService.client
    private val context: Context
    private val _rekeningWithTodaySetoran = MutableStateFlow<List<Rekening>>(emptyList())
    val rekeningWithTodaySetoran: StateFlow<List<Rekening>> = _rekeningWithTodaySetoran

    private val headerExportTable = arrayOf(
        "NoRekening",
        "Nama",
        "TglTrans",
        "Setoran"
    )

    init {
        this.context = application.applicationContext
    }

    suspend fun getRekeningFromNoRek(s: String): Result<Rekening> {
        return withContext(Dispatchers.IO) {
            try {
                val columns = Columns.raw("""
                    no_rek,
                    angsuran,
                    pinjaman_awal,
                    anggota (
                        nama,
                        no_ktp
                    )
                """.trimIndent())
                val rekening = supabase.from("rekening").select(
                    columns = columns
                ) {
                    filter {
                        eq("no_rek", s)
                    }
                }.decodeSingle<Rekening>()
                Log.d("ScanActivity", "Rekening found: $rekening")

                Result.success(rekening)
            } catch (e: Exception) {
                // Handle error (log, throw custom exception, return emptyList)
                e.printStackTrace()
                Log.d("ScanActivity", "Error: ${e.printStackTrace()}")
                Log.d("ScanActivity", "Rekening not found. Return empty instead")
                Result.failure(e)
            }
        }
    }

    suspend fun getAllRekening(): List<Rekening> {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("rekening").select {
                    order("no_rek", order = Order.ASCENDING)
                }.decodeList<Rekening>()
            } catch (e: Exception) {
                // Handle error (log, throw custom exception, return emptyList)
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun updateRekening(rekening: Rekening) {
        withContext(Dispatchers.IO) {
            try {
                supabase.from("rekening").update({
                    set("tgl_trans", rekening.tglTrans)
                }) {
                    filter {
                        eq("no_rek", rekening.noRek)
                    }
                }
            } catch (e: Exception) {
                // Handle error (log, throw custom exception, return emptyList)
                e.printStackTrace()
            }
        }
    }

    suspend fun addSetoran(transaksi: Transaksi): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("setoran").insert(transaksi)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun getNumberOfScan(): Int {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("rekening").select {
                    count(Count.EXACT)
                }.decodeSingle<Int>()
            } catch (e: Exception) {
                // Handle error (log, throw custom exception, return emptyList)
                e.printStackTrace()
                0
            }
        }
    }

    suspend fun getRekeningWithTodaySetoran(): Result<List<Rekening>> = withContext(Dispatchers.IO) {
        try {
            val timeZone = TimeZone.currentSystemDefault()
            val todayStr = Clock.System.now().toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)

            val columns = Columns.raw("""
                no_rek,
                anggota ( nama ),
                setoran ( setoran, created_at )
            """.trimIndent())

            val setoran = supabase.from("rekening").select(columns) {
                filter {
                    gte("setoran.created_at", todayStr.toString())
                }
            }

            val rawJson = setoran.decodeList<JsonElement>()
            Log.d("RekeningRepo", "Raw JSON: $rawJson")
            Log.d("RekeningRepo", "Fetched ${setoran.decodeList<Rekening>().size} setoran")
            _rekeningWithTodaySetoran.value = setoran.decodeList<Rekening>()
            Result.success(setoran.decodeList<Rekening>())
        } catch (e: Exception) {
            Log.e("RekeningRepo", "Error fetching rekening with today setoran: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun exportToXls(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        Log.d("RekeningRepo", "Exporting to XLS...")
        try {
            getRekeningWithTodaySetoran()

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault())
            val workbook: Workbook = HSSFWorkbook()
            val sheet = workbook.createSheet()

            // Create excel header
            val row0 = sheet.createRow(0)

            // Excel header
            var cellnum = 0
            for (title in headerExportTable) {
                val cell = row0.createCell(cellnum++)
                cell.setCellValue(title)
            }

            // Rest of excel file
            var rownum = 1
            for (rekening in rekeningWithTodaySetoran.value) {
                val setoran = rekening.setoran?.getOrNull(0)

                val row = sheet.createRow(rownum++)

                val cellNoRek = row.createCell(0)
                val cellNama = row.createCell(1)
                val cellTgl = row.createCell(2)
                val cellSetoran = row.createCell(3)

                val date = if (setoran != null && setoran.tglTrans != null)
                    formatter.format(setoran.tglTrans.toJavaInstant())
                else "-"

                // 4. Safely chain nested relationships
                val namaAnggota = rekening.anggota?.nama ?: "-"
                val setoranAmount = setoran?.setoran ?: 0

                cellNoRek.setCellValue(rekening.noRek)
                cellNama.setCellValue(namaAnggota)
                cellTgl.setCellValue(date)
                cellSetoran.setCellValue(setoranAmount.toDouble())
            }

            val pickedDir = DocumentFile.fromTreeUri(context, uri)
            if (pickedDir != null) {
                val newFile = pickedDir.createFile(
                    "application/vnd.ms-excel",
                    "Export_" + DateHelper.currentDateString
                )

                if (newFile != null) {
                    val out = context.contentResolver.openOutputStream(newFile.uri)
                    workbook.write(out)
                    out?.close()
                }
            }
            Result.success(Unit)
        } catch (e: RestException) {
            Log.d("RekeningRepo", "Error: ${e.message}")
            Result.failure(Exception("Gagal menarik data dari Supabase: ${e.error}"))
        } catch (e: IOException) {
            Log.d("RekeningRepo", "Error: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.d("RekeningRepo", "Error: ${e.message}")
            Result.failure(e)
        }
    }
}
