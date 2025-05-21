package com.example.proyeksp.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyeksp.database.AppDatabase
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.database.RekeningDAO
import com.example.proyeksp.helper.DateHelper
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RekeningRepo(application: Application) {
    private val rekeningDAO: RekeningDAO?
    private val context: Context
    val rekeningList: LiveData<List<Rekening?>?>?
    private var success = MutableLiveData<Boolean>()
    private val executorService: ExecutorService =
        Executors.newSingleThreadExecutor()
    private val headerExportTable = arrayOf(
        "NoRekening",
        "Nama",
        "TglTrans",
        "Setoran"
    )

    init {
        val db: AppDatabase = AppDatabase.Companion.getDatabase(application)
        this.rekeningDAO = db.rekeningDao()
        rekeningList = rekeningDAO.allRekening

        this.context = application.applicationContext
    }

    fun getSuccess(): LiveData<Boolean> {
        return success
    }

    fun findRekeningByNoRek(s: String?): Rekening? {
        val future = executorService.submit<Rekening?> {
            rekeningDAO!!.getRekeningByNoRek(
                s
            )
        }
        try {
            return future.get()
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    val daftarRekening: LiveData<List<Rekening?>?>?
        get() = rekeningDAO.getDaftarRekening()

    fun update(rekening: Rekening?) {
        executorService.execute { rekeningDAO!!.update(rekening) }
    }

    val scanData: LiveData<Int?>?
        get() = rekeningDAO.getScanData()

    val totalSetoran: LiveData<Long?>?
        get() = rekeningDAO.getTotalSetoran()

    fun exportToXls(uri: Uri) {
        executorService.execute {
            val formatter: DateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val workbook: Workbook = HSSFWorkbook()

            val sheet = workbook.createSheet()

            val rekeningList = rekeningDAO.getRekeningExport()

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
                var date = if (rekening.tglTrans == 0L) "-"
                else formatter.format(Date(rekening.tglTrans))

                cellNoRek.setCellValue(rekening!!.noRek)
                cellNama.setCellValue(rekening.nama)
                cellTgl.setCellValue(date)
                cellSetoran.setCellValue(rekening.setoran.toDouble())
            }
            try {
                val pickedDir = DocumentFile.fromTreeUri(context, uri)
                if (pickedDir != null) {
                    val newFile = pickedDir.createFile(
                        "application/vnd.ms-excel",
                        "Export_" + DateHelper.getCurrentDateString()
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
