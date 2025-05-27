package com.example.proyeksp.database

import kotlinx.serialization.Serializable

@Serializable
data class Transaksi (
    val id: Long,
    val noRek: String,
    val tglTrans: Long,
    val setoran: Long,
)