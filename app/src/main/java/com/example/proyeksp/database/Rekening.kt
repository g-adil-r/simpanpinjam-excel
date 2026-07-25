package com.example.proyeksp.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize
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

    var setoran: List<Transaksi>? = null,

    var anggota: Anggota? = null
)