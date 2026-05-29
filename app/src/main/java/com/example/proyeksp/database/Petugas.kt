package com.example.proyeksp.database

import kotlinx.serialization.Serializable

@Serializable
data class Petugas (
    var userid: String? = null,

    var username: String? = null,

    var role: String? = null,
) {
    fun isAdmin(): Boolean {
        return this.role?.lowercase() == "admin"
    }
}