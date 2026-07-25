package com.example.proyeksp.database

import kotlinx.serialization.Serializable

@Serializable
data class Anggota(
    var nama: String? = null,

    var angsuran: Long = 0L
)