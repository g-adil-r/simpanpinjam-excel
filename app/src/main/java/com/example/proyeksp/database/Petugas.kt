package com.example.proyeksp.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Petugas (
    var id: Long? = null,

    @SerialName("nama_lengkap")
    var namaLengkap: String? = null,

    var username: String? = null,

    @SerialName("no_telp")
    var noTelp: String? = null,

    @SerialName("no_ktp")
    var noKtp: String? = null,

    var alamat: String? = null,

    var role: String? = null,
) : Parcelable {
    fun isAdmin(): Boolean {
        return this.role?.lowercase() == "admin"
    }
}