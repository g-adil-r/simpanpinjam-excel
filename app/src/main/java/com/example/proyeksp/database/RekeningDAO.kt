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
    // Parameters for insert/update/delete should generally be non-null
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(rekening: Rekening) // Changed from Rekening?

    @Update
    fun update(rekening: Rekening) // Changed from Rekening?

    @Delete
    fun delete(rekening: Rekening) // Changed from Rekening?

    // Return types:
    // - List<Rekening> instead of List<Rekening?> if your query guarantees non-null items.
    // - LiveData<List<Rekening>> instead of LiveData<List<Rekening?>?>? if the LiveData and list are always expected.
    @get:Query("SELECT * FROM rekening ORDER BY no_rek ASC")
    val allRekening: LiveData<List<Rekening>> // Assuming Rekening objects in list are non-null

    @get:Query("SELECT * FROM rekening ORDER BY no_rek ASC")
    val rekeningExport: List<Rekening> // Assuming Rekening objects in list are non-null

    @get:Query(
        ("SELECT * FROM rekening " +
                "ORDER BY " +
                "CASE " +
                "   WHEN tgl_trans = 0 THEN 1 " +
                "   ELSE 0 " +
                "END, tgl_trans DESC, nama;")
    )
    val daftarRekening: LiveData<List<Rekening>> // Assuming Rekening objects in list are non-null

    // Parameter noRek should be non-null if you are querying by a primary key.
    // The return type Rekening? is correct as the record might not exist.
    @Query("SELECT * FROM rekening WHERE no_rek = :noRek")
    fun getRekeningByNoRek(noRek: String): Rekening? // Changed noRek parameter to String

    // COUNT(*) will always return an Int (0 or more), not nullable.
    @get:Query("SELECT COUNT(*) FROM rekening WHERE setoran > 0")
    val scanData: LiveData<Int> // Changed from LiveData<Int?>?

    // SUM() can return null if there are no rows or all values are null.
    @get:Query("SELECT SUM(setoran) FROM rekening")
    val totalSetoran: LiveData<Long?> // LiveData<Long?> is correct. Property itself non-null.

    @Query("DELETE FROM rekening")
    fun removeAll()
}

//@Dao
//interface RekeningDAO {
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun insert(rekening: Rekening?)
//
//    @Update
//    fun update(rekening: Rekening?)
//
//    @Delete
//    fun delete(rekening: Rekening?)
//
//    @get:Query("SELECT * FROM rekening ORDER BY no_rek ASC")
//    val allRekening: LiveData<List<Rekening?>?>?
//
//    @get:Query("SELECT * FROM rekening ORDER BY no_rek ASC")
//    val rekeningExport: List<Rekening?>?
//
//    @get:Query(
//        ("SELECT * FROM rekening " +
//                "ORDER BY " +
//                "CASE " +
//                "   WHEN tgl_trans = 0 THEN 1 " +
//                "   ELSE 0 " +
//                "END, tgl_trans DESC, nama;")
//    )
//    val daftarRekening: LiveData<List<Rekening?>?>?
//
//    @Query("SELECT * FROM rekening WHERE no_rek = :noRek")
//    fun getRekeningByNoRek(noRek: String?): Rekening?
//
//    @get:Query("SELECT COUNT(*) FROM rekening WHERE setoran > 0")
//    val scanData: LiveData<Int?>?
//
//    @get:Query("SELECT SUM(setoran) FROM rekening")
//    val totalSetoran: LiveData<Long?>?
//
//    @Query("DELETE FROM rekening")
//    fun removeAll()
//}