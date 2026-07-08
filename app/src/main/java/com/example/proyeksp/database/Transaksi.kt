package com.example.proyeksp.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaksi (
    val id: Long? = null,

    @SerialName("no_rek")
    val noRek: String? = null,

//    @SerialName("created_at")
//    val tglTrans: Long,

    val setoran: Long? = null,

    @SerialName("petugas_id")
    val petugasId: Long? = null,

    val rekening: Rekening? = null
)