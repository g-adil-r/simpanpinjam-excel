package com.example.proyeksp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RekeningDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Rekening rekening);

    @Update()
    void update(Rekening rekening);

    @Delete()
    void delete(Rekening rekening);

    @Query("SELECT * FROM rekening ORDER BY no_rek ASC")
    LiveData<List<Rekening>> getAllRekening();

    @Query("SELECT * FROM rekening ORDER BY no_rek ASC")
    List<Rekening> getRekeningExport();

    @Query("SELECT * FROM rekening " +
            "ORDER BY " +
            "CASE " +
            "   WHEN tgl_trans = 0 THEN 1 " +
            "   ELSE 0 " +
            "END, nama;")
    LiveData<List<Rekening>> getDaftarRekening();

    @Query("SELECT * FROM rekening WHERE no_rek = :noRek")
    Rekening getRekeningByNoRek(String noRek);

    @Query("SELECT COUNT(*) FROM rekening WHERE setoran > 0")
    LiveData<Integer> getScanData();

    @Query("SELECT SUM(setoran) FROM rekening")
    LiveData<Long> getTotalSetoran();

    @Query("DELETE FROM rekening")
    void removeAll();
}
