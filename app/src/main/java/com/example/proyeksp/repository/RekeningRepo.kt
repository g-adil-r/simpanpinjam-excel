package com.example.proyeksp.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyeksp.database.AppDatabase
import com.example.proyeksp.database.Nasabah
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.database.RekeningDAO
import com.example.proyeksp.database.SupabaseService
import com.example.proyeksp.database.Transaksi
import com.example.proyeksp.helper.DateHelper
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.lang.reflect.Array.set
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RekeningRepo(application: Application) {
    private val supabase = SupabaseService.client
    private val rekeningDAO: RekeningDAO?
    private val context: Context
    val rekeningList: LiveData<List<Rekening>>
    private var success = MutableLiveData<Boolean>()
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val headerExportTable = arrayOf(
        "NoRekening",
        "Nama",
        "TglTrans",
        "Setoran"
    )

    init {
        val db: AppDatabase = AppDatabase.getDatabase(application)
        this.rekeningDAO = db.rekeningDao()
        rekeningList = rekeningDAO.allRekening

        this.context = application.applicationContext
    }

    fun getSuccess(): LiveData<Boolean> {
        return success
    }

//    fun findRekeningByNoRek(s: String): Rekening? {
//        val future = executorService.submit<Rekening?> {
//            rekeningDAO!!.getRekeningByNoRek(s)
//        }
//        try {
//            return future.get()
//        } catch (e: ExecutionException) {
//            throw RuntimeException(e)
//        } catch (e: InterruptedException) {
//            throw RuntimeException(e)
//        }
//    }

    suspend fun getRekeningFromNoRek(s: String): Rekening {
        return withContext(Dispatchers.IO) {
            try {
                val columns = Columns.raw("""
                    no_rek,
                    nama,
                    saldo_simpanan,
                    saldo_pinjaman,
                    angsuran
                """.trimIndent())
                val rekening = supabase.from("nasabah").select(
                    columns = columns
                ) {
                    filter {
                        eq("no_rek", s)
                    }
                }.decodeSingle<Rekening>()
                Log.d("ScanActivity", "Rekening found: $rekening")
                rekening
            } catch (e: Exception) {
                // Handle error (log, throw custom exception, return emptyList)
                e.printStackTrace()
                Log.d("ScanActivity", "Error: ${e.printStackTrace()}")
                Log.d("ScanActivity", "Rekening not found. Return empty instead")
                Rekening("", "", 0, 0, 0)
            }
        }
    }

    suspend fun getAllRekening(): List<Rekening> {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("Rekening").select {
                    order("no_rek", order = Order.ASCENDING)
                }.decodeList<Rekening>()
            } catch (e: Exception) {
                // Handle error (log, throw custom exception, return emptyList)
                e.printStackTrace()
                emptyList()
            }
        }
    }

//    val daftarRekening: LiveData<List<Rekening>>?
//        get() = rekeningDAO?.daftarRekening

//    fun update(rekening: Rekening) {
//        executorService.execute { rekeningDAO!!.update(rekening) }
//    }

    suspend fun updateRekening(rekening: Rekening) {
        withContext(Dispatchers.IO) {
            try {
                supabase.from("Rekening").update({
                    set("tgl_trans", rekening.tglTrans)
                    set("setoran", rekening.setoran)
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

    suspend fun addSetoran(transaksi: Transaksi) {
        withContext(Dispatchers.IO) {
            try {
                supabase.from("transaksi").upsert(transaksi)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val scanData: LiveData<Int>?
        get() = rekeningDAO?.scanData

    suspend fun getNumberOfScan(): Int {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("Rekening").select {
                    count(Count.EXACT)
                }.decodeSingle<Int>()
            } catch (e: Exception) {
                // Handle error (log, throw custom exception, return emptyList)
                e.printStackTrace()
                0
            }
        }
    }

    val totalSetoran: LiveData<Long?>?
        get() = rekeningDAO?.totalSetoran

    fun exportToXls(uri: Uri) {
        executorService.execute {
            val formatter: DateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val workbook: Workbook = HSSFWorkbook()

            val sheet = workbook.createSheet()

            val rekeningList = rekeningDAO?.rekeningExport

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
            for (rekening in rekeningList!!) {
                val row = sheet.createRow(rownum++)

                val cellNoRek = row.createCell(0)
                val cellNama = row.createCell(1)
                val cellTgl = row.createCell(2)
                val cellSetoran = row.createCell(3)
                val date = if (rekening.tglTrans == 0L) "-"
                else Date(rekening.tglTrans).let { formatter.format(it) }

                cellNoRek.setCellValue(rekening.noRek)
                cellNama.setCellValue(rekening.nama)
                cellTgl.setCellValue(date)
                cellSetoran.setCellValue(rekening.setoran.toDouble())
            }
            try {
                val pickedDir = DocumentFile.fromTreeUri(context, uri)
                if (pickedDir != null) {
                    val newFile = pickedDir.createFile(
                        "application/vnd.ms-excel",
                        "Export_" + DateHelper.currentDateString
                    )

                    val out = context.contentResolver.openOutputStream(
                        newFile!!.uri
                    )
                    workbook.write(out)
                    out!!.close()
                    success.postValue(true)
                    success = MutableLiveData()
                }
            } catch (e: IOException) {
                e.stackTrace
            }
        }
    }

    @Throws(RuntimeException::class)
    fun importFromXlsx(uri: Uri) {
        executorService.execute {
            try {
                rekeningDAO!!.removeAll()
                val inputStream = context.contentResolver.openInputStream(uri)

                val workbook: Workbook = XSSFWorkbook(inputStream)

                val sheet = workbook.getSheetAt(0)
                val rows: Iterator<Row> =
                    sheet.iterator()
                rows.next() // Skips the header row

                while (rows.hasNext()) {
                    val row = rows.next()
                    val newRekening = Rekening(
                        row.getCell(0).stringCellValue,
                        row.getCell(1).stringCellValue,
                        row.getCell(2).numericCellValue.toLong(),
                        row.getCell(3).numericCellValue.toLong(),
                        row.getCell(4).numericCellValue.toLong()
                    )

                    rekeningDAO.insert(newRekening)
                }
                inputStream!!.close()
                success.postValue(true)
                success = MutableLiveData()
            } catch (e: IOException) {
                e.stackTrace
            } catch (e: RuntimeException) {
                success.postValue(false)
                success = MutableLiveData()
            }
        }
    }
}
