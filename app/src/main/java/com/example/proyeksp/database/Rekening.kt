package com.example.proyeksp.database

import androidx.room.ColumnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rekening(
    @SerialName("no_rek")
    var noRek: String,

    var nama: String? = null,

    @SerialName("pinjaman_awal")
    var pinjamanAwal: Long? = 0L,

    @ColumnInfo(name = "angsuran")
    var angsuran: Long? = 0L,

    var transaksi: List<Transaksi>? = null,

    var anggota: Anggota? = null
)