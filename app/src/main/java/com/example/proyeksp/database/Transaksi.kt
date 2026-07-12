package com.example.proyeksp.database

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaksi (
    val id: Long? = null,

    @SerialName("no_rek")
    val noRek: String? = null,

    @SerialName("created_at")
    val tglTrans: Instant? = null,

    val setoran: Long? = null,

    @SerialName("petugas_id")
    val petugasId: Long? = null,

    val rekening: Rekening? = null
)