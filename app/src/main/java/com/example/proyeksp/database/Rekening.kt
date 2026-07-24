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

    @SerialName("saldo_simpanan")
    var saldoSimpanan: Long? = 0L,

    @SerialName("saldo_pinjaman")
    var saldoPinjaman: Long? = 0L,

    @SerialName("pinjaman_awal")
    var pinjamanAwal: Long? = 0L,

    @ColumnInfo(name = "angsuran")
    var angsuran: Long? = 0L,

    @SerialName("tgl_trans")
    var tglTrans: Long? = 0L,

    var setoran: List<Transaksi>? = null,

    var anggota: Anggota? = null
)