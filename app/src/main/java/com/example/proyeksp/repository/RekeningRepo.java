package com.example.proyeksp.repository;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyeksp.database.AppDatabase;
import com.example.proyeksp.database.Rekening;
import com.example.proyeksp.database.RekeningDAO;
import com.example.proyeksp.helper.DateHelper;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RekeningRepo {
    private final RekeningDAO rekeningDAO;
    private final Context context;
    private LiveData<List<Rekening>> rekeningList;
    private MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final ExecutorService executorService;
    private final String[] headerExportTable = {
            "NoRekening",
            "Nama",
            "TglTrans",
            "Setoran"
    };

    public RekeningRepo(Application application) {
        executorService = Executors.newSingleThreadExecutor();

        AppDatabase db = AppDatabase.getDatabase(application);
        this.rekeningDAO = db.rekeningDao();
        rekeningList = rekeningDAO.allRekening;

        this.context = application.getApplicationContext();
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }
    public LiveData<List<Rekening>> getRekeningList() {
        return rekeningList;
    }
    public Rekening findRekeningByNoRek(String s) {
        Future<Rekening> future = executorService.submit(() -> rekeningDAO.getRekeningByNoRek(s));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public LiveData<List<Rekening>> getDaftarRekening() {
        return rekeningDAO.daftarRekening;
    }

    public void update(final Rekening rekening) {
        executorService.execute(() -> this.rekeningDAO.update(rekening));
    }

    public LiveData<Integer> getScanData() { return rekeningDAO.scanData; }

    public LiveData<Long> getTotalSetoran() { return rekeningDAO.totalSetoran; }

    public void exportToXls(Uri uri) {
        executorService.execute(() -> {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Workbook workbook = new HSSFWorkbook();

            Sheet sheet = workbook.createSheet();

            List<Rekening> rekeningList = rekeningDAO.rekeningExport;

            // Create excel header
            Row row0 = sheet.createRow(0);

            // Excel header
            int cellnum = 0;
            for (String title:headerExportTable) {
                Cell cell = row0.createCell(cellnum++);

                cell.setCellValue(title);
            }

            // Rest of excel file
            int rownum = 1;
            for (Rekening rekening: rekeningList) {
                Row row = sheet.createRow(rownum++);

                Cell cellNoRek = row.createCell(0);
                Cell cellNama = row.createCell(1);
                Cell cellTgl = row.createCell(2);
                Cell cellSetoran = row.createCell(3);

                String date;
                if (rekening.tglTrans == 0) date = "-";
                else date = formatter.format(new Date(rekening.tglTrans));

                cellNoRek.setCellValue(rekening.getNoRek());
                cellNama.setCellValue(rekening.nama);
                cellTgl.setCellValue(date);
                cellSetoran.setCellValue(rekening.setoran);
            }

            try {
                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, uri);
                if (pickedDir != null) {
                    DocumentFile newFile = pickedDir.createFile(
                            "application/vnd.ms-excel",
                            "Export_"+ DateHelper.getCurrentDateString());

                    OutputStream out = context.getContentResolver().openOutputStream(newFile.getUri());
                    workbook.write(out);
                    out.close();
                    success.postValue(true);
                    success = new MutableLiveData<>();
                }
            } catch (IOException e) {
                e.getStackTrace();
            }
        });
    }

    public void importFromXlsx(Uri uri) throws RuntimeException {
        executorService.execute(() -> {
            try {
                rekeningDAO.removeAll();
                InputStream inputStream = context.getContentResolver().openInputStream(uri);

                Workbook workbook = new XSSFWorkbook(inputStream);

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                rows.next(); // Skips the header row

                while (rows.hasNext()) {
                    Row row = rows.next();
                    Rekening newRekening = new Rekening(
                            row.getCell(0).getStringCellValue(),
                            row.getCell(1).getStringCellValue(),
                            (long)row.getCell(2).getNumericCellValue(),
                            (long)row.getCell(3).getNumericCellValue(),
                            (long)row.getCell(4).getNumericCellValue()
                    );

                    rekeningDAO.insert(newRekening);
                }
                inputStream.close();
                success.postValue(true);
                success = new MutableLiveData<>();
            } catch (IOException e) {
                e.getStackTrace();
            } catch (RuntimeException e) {
                success.postValue(false);
                success = new MutableLiveData<>();
            }
        });
    }
}
