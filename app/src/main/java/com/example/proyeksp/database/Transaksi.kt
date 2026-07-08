package com.example.proyeksp.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaksi (
    val id: Long? = null,

    @SerialName("no_rek")
    val noRek: String,

//    @SerialName("created_at")
//    val tglTrans: Long,

    val setoran: Long,

    @SerialName("petugas_id")
    val petugasId: Long
)