package com.example.proyeksp.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Nasabah(
    @SerialName("no_rek")
    var noRek: String,

    var nama: String? = null,

    @SerialName("saldo_simpanan")
    var saldoSimpanan: Long = 0L,

    @SerialName("saldo_pinjaman")
    var saldoPinjaman: Long = 0L,

    var angsuran: Long = 0L
)