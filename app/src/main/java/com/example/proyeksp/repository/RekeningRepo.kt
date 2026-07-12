package com.example.proyeksp.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonElement
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RekeningRepo(application: Application) {
    private val supabase = SupabaseService.client
//    private val rekeningDAO: RekeningDAO?
    private val context: Context
    // 1. Initialize as a MutableLiveData
    private val _rekeningList = MutableLiveData<List<Rekening>>()
    private val _rekeningWithTodaySetoran = MutableStateFlow<List<Rekening>>(emptyList())
    val rekeningWithTodaySetoran: StateFlow<List<Rekening>> = _rekeningWithTodaySetoran

    val rekeningList: LiveData<List<Rekening>> = _rekeningList
    private var success = MutableLiveData<Boolean>()
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val headerExportTable = arrayOf(
        "NoRekening",
        "Nama",
        "TglTrans",
        "Setoran"
    )

    init {
//        val db: AppDatabase = AppDatabase.getDatabase(application)
//        this.rekeningDAO = db.rekeningDao()
//        rekeningList = rekeningDAO.allRekening

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

//    val daftarRekening: LiveData<List<Rekening>>?
//        get() = rekeningDAO?.daftarRekening

//    fun update(rekening: Rekening) {
//        executorService.execute { rekeningDAO!!.update(rekening) }
//    }

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

//    val scanData: LiveData<Int>?
//        get() = rekeningDAO?.scanData

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


    suspend fun getSetoran(): List<Transaksi> {
        return withContext(Dispatchers.IO) {
            try {
                val columns = Columns.raw("""
                    id,
                    setoran,
                    rekening (
                        no_rek,
                        anggota (nama)
                    )
                """.trimIndent())
                val setoran = supabase.from("setoran").select(
                    columns
                )
                val rawJson = setoran.decodeList<JsonElement>()
                Log.d("RekeningRepo", "Raw JSON: $rawJson")
                Log.d("RekeningRepo", "Fetched ${setoran.decodeList<Transaksi>().size} setoran")
                setoran.decodeList<Transaksi>()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("RekeningRepo", "Error fetching setoran: ${e.message}")
                emptyList() // Return empty list on failure to avoid crashes
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
            Result.success(setoran.decodeList<Rekening>())
        } catch (e: Exception) {
            Log.e("RekeningRepo", "Error fetching rekening with today setoran: ${e.message}")
            Result.failure(e)
        }
    }


//    val totalSetoran: LiveData<Long?>?
//        get() = rekeningDAO?.totalSetoran

//    fun exportToXls(uri: Uri) {
//        executorService.execute {
//            val formatter: DateFormat =
//                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//            val workbook: Workbook = HSSFWorkbook()
//
//            val sheet = workbook.createSheet()
//
////            val rekeningList = _setoranList
//
//            // Create excel header
//            val row0 = sheet.createRow(0)
//
//            // Excel header
//            var cellnum = 0
//            for (title in headerExportTable) {
//                val cell = row0.createCell(cellnum++)
//
//                cell.setCellValue(title)
//            }
//
//            // Rest of excel file
//            var rownum = 1
//            for (setoran in _setoranList) {
//                val row = sheet.createRow(rownum++)
//
//                val cellNoRek = row.createCell(0)
//                val cellNama = row.createCell(1)
//                val cellTgl = row.createCell(2)
//                val cellSetoran = row.createCell(3)
//                val date = if (setoran.tglTrans == 0L) "-"
//                else Date(setoran.tglTrans).let { formatter.format(it) }
//
//                cellNoRek.setCellValue(setoran.noRek)
//                cellNama.setCellValue(rekening.nama)
//                cellTgl.setCellValue(date)
//                cellSetoran.setCellValue(rekening.setoran.toDouble())
//            }
//            try {
//                val pickedDir = DocumentFile.fromTreeUri(context, uri)
//                if (pickedDir != null) {
//                    val newFile = pickedDir.createFile(
//                        "application/vnd.ms-excel",
//                        "Export_" + DateHelper.currentDateString
//                    )
//
//                    val out = context.contentResolver.openOutputStream(
//                        newFile!!.uri
//                    )
//                    workbook.write(out)
//                    out!!.close()
//                    success.postValue(true)
//                    success = MutableLiveData()
//                }
//            } catch (e: IOException) {
//                e.stackTrace
//            }
//        }
//    }

//    fun exportToXls(uri: Uri): Result<Unit> {
//        // 1. Read the list value on the Main Thread safely before going to background
//        val setoranList = _setoranList.value ?: return Result.failure(RuntimeException("No data to export"))
//
//        executorService.execute {
//            val formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//            val workbook: Workbook = HSSFWorkbook()
//            val sheet = workbook.createSheet()
//
//            // Create excel header
//            val row0 = sheet.createRow(0)
//
//            // Excel header
//            var cellnum = 0
//            for (title in headerExportTable) {
//                val cell = row0.createCell(cellnum++)
//                cell.setCellValue(title)
//            }
//
//            // Rest of excel file
//            var rownum = 1
//            for (setoran in setoranList) { // 2. Iterate over the extracted list, not the LiveData
//                val row = sheet.createRow(rownum++)
//
//                val cellNoRek = row.createCell(0)
//                val cellNama = row.createCell(1)
//                val cellTgl = row.createCell(2)
//                val cellSetoran = row.createCell(3)
//
//                // 3. Safe ISO-8601 Date Parsing
////                val date = if (setoran.tglTrans.isNullOrEmpty()) {
////                    "-"
////                } else {
////                    try {
////                        // Supabase sends TIMESTAMPTZ as "yyyy-MM-dd'T'HH:mm:ss..."
////                        val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
////                            timeZone = TimeZone.getTimeZone("UTC") // Supabase time is always UTC
////                        }
////                        val parsedDate = isoParser.parse(setoran.tglTrans)
////                        parsedDate?.let { formatter.format(it) } ?: "-"
////                    } catch (e: Exception) {
////                        "-" // Fallback if parsing fails
////                    }
////                }
//                val date = "-"
//
//                // 4. Safely chain nested relationships
//                val namaAnggota = setoran.rekening?.anggota?.nama ?: "-"
//                val setoranAmount = setoran.setoran?.toDouble() ?: 0.0
//
//                cellNoRek.setCellValue(setoran.noRek)
//                cellNama.setCellValue(namaAnggota)
//                cellTgl.setCellValue(date)
//                cellSetoran.setCellValue(setoranAmount)
//            }
//
//            try {
//                val pickedDir = DocumentFile.fromTreeUri(context, uri)
//                if (pickedDir != null) {
//                    val newFile = pickedDir.createFile(
//                        "application/vnd.ms-excel",
//                        "Export_" + DateHelper.currentDateString
//                    )
//
//                    if (newFile != null) {
//                        val out = context.contentResolver.openOutputStream(newFile.uri)
//                        workbook.write(out)
//                        out?.close()
//
//                        // 5. Signal success safely
//                        success.postValue(true)
//
//                        // ⚠️ DO NOT reassign success = MutableLiveData() here,
//                        // as it breaks the UI observers bound to the original object.
//                    }
//
//                    Result.success(Unit)
//                }
//            } catch (e: IOException) {
//                Log.e("ExportExcel", "Export failed: ${e.message}")
//                Result.failure(Exception("Export failed"))
//            }
//        }
//    }

    suspend fun exportToXls(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        Log.d("RekeningRepo", "Exporting to XLS...")
        try {
            val todayISO = ""

            val columns = Columns.raw("""
            setoran,
            created_at,
            rekening (
                no_rek,
                anggota (
                    nama
                )
            )
            """.trimIndent())

            // 3. Tarik data transaksi khusus HARI INI dari Supabase
            val transaksiList = SupabaseService.client.from("setoran")
                .select(columns = columns)

            Log.d("RekeningRepo", "Fetched ${transaksiList.decodeList<Transaksi>().size} setoran")
            Log.d("RekeningRepo", "Data: ${transaksiList.decodeList<JsonElement>()}")

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

    @Throws(RuntimeException::class)
    fun importFromXlsx(uri: Uri) {
        executorService.execute {
            try {
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

//                    rekeningDAO.insert(newRekening)
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
