package com.example.proyeksp.database

import kotlinx.serialization.Serializable

@Serializable
data class Anggota(
    var id: Long? = null,
    var nama: String? = null,
    var noTelp: String? = null,
    var noKTP: String? = null,
    var alamat: String? = null
)