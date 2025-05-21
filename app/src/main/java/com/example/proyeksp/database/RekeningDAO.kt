package com.example.proyeksp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RekeningDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(rekening: Rekening?)

    @Update
    fun update(rekening: Rekening?)

    @Delete
    fun delete(rekening: Rekening?)

    @get:Query("SELECT * FROM rekening ORDER BY no_rek ASC")
    val allRekening: LiveData<List<Rekening?>?>?

    @get:Query("SELECT * FROM rekening ORDER BY no_rek ASC")
    val rekeningExport: List<Rekening?>?

    @get:Query(
        ("SELECT * FROM rekening " +
                "ORDER BY " +
                "CASE " +
                "   WHEN tgl_trans = 0 THEN 1 " +
                "   ELSE 0 " +
                "END, tgl_trans DESC, nama;")
    )
    val daftarRekening: LiveData<List<Rekening?>?>?

    @Query("SELECT * FROM rekening WHERE no_rek = :noRek")
    fun getRekeningByNoRek(noRek: String?): Rekening?

    @get:Query("SELECT COUNT(*) FROM rekening WHERE setoran > 0")
    val scanData: LiveData<Int?>?

    @get:Query("SELECT SUM(setoran) FROM rekening")
    val totalSetoran: LiveData<Long?>?

    @Query("DELETE FROM rekening")
    fun removeAll()
}
