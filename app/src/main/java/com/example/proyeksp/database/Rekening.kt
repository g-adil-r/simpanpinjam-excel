package com.example.proyeksp.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize // Import for @Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize // Use Kotlin's @Parcelize for simpler Parcelable implementation
@Entity(
    tableName = "rekening",
    indices = [Index(name = "no_rek_index", value = ["no_rek"], unique = true)]
)
@Serializable
data class Rekening( // Changed to a data class - highly recommended for entities
    @PrimaryKey
    @ColumnInfo(name = "no_rek")
    @SerialName("no_rek")
    var noRek: String, // << CHANGED: Now non-nullable String

    @ColumnInfo(name = "nama")
    var nama: String? = null, // Can remain nullable if 'nama' can be absent

    @ColumnInfo(name = "saldo_simpanan")
    @SerialName("saldo_simpanan")
    var saldoSimpanan: Long = 0L, // Use L for Long literals for clarity

    @ColumnInfo(name = "saldo_pinjaman")
    @SerialName("saldo_pinjaman")
    var saldoPinjaman: Long = 0L,

    @ColumnInfo(name = "angsuran")
    var angsuran: Long = 0L,

    @ColumnInfo(name = "tgl_trans")
    @SerialName("tgl_trans")
    var tglTrans: Long = 0L,

    @ColumnInfo(name = "setoran")
    var setoran: Long = 0L
) : Parcelable {
    constructor(
        noRek: String,
        nama: String?,
        saldoSimpanan: Long,
        saldoPinjaman: Long,
        angsuran: Long
    ) : this( // Delegate to the primary constructor
        noRek = noRek,
        nama = nama,
        saldoSimpanan = saldoSimpanan,
        saldoPinjaman = saldoPinjaman,
        angsuran = angsuran,
        tglTrans = 0L, // Default values for fields not in this constructor
        setoran = 0L
    )

//    constructor(
//        noRek: String,
//        nama: String?,
//        saldoSimpanan: Long,
//        saldoPinjaman: Long,
//        angsuran: Long
//    ) {
//        this.noRek = noRek
//        this.nama = nama
//        this.saldoSimpanan = saldoSimpanan
//        this.saldoPinjaman = saldoPinjaman
//        this.angsuran = angsuran
//        this.tglTrans = 0
//        this.setoran = 0
//    }

    // @Parcelize handles all Parcelable boilerplate.
    // So, you can remove:
    // - protected constructor(`in`: Parcel)
    // - override fun describeContents(): Int
    // - override fun writeToParcel(dest: Parcel, flags: Int)
    // - companion object CREATOR
}

//    @PrimaryKey
//    @ColumnInfo(name = "no_rek")
//    var noRek: String? = null
//
//    @ColumnInfo(name = "nama")
//    var nama: String? = null
//
//    @ColumnInfo(name = "saldo_simpanan")
//    var saldoSimpanan: Long = 0
//
//    @ColumnInfo(name = "saldo_pinjaman")
//    var saldoPinjaman: Long = 0
//
//    @ColumnInfo(name = "angsuran")
//    var angsuran: Long = 0
//
//    @ColumnInfo(name = "tgl_trans")
//    var tglTrans: Long = 0
//
//    @ColumnInfo(name = "setoran")
//    var setoran: Long = 0
//
//    constructor()
//
//    constructor(
//        noRek: String,
//        nama: String?,
//        saldoSimpanan: Long,
//        saldoPinjaman: Long,
//        angsuran: Long
//    ) {
//        this.noRek = noRek
//        this.nama = nama
//        this.saldoSimpanan = saldoSimpanan
//        this.saldoPinjaman = saldoPinjaman
//        this.angsuran = angsuran
//        this.tglTrans = 0
//        this.setoran = 0
//    }
//
//    protected constructor(`in`: Parcel) {
//        noRek = `in`.readString()
//        nama = `in`.readString()
//        saldoSimpanan = `in`.readInt().toLong()
//        saldoPinjaman = `in`.readInt().toLong()
//        angsuran = `in`.readInt().toLong()
//        tglTrans = `in`.readLong()
//        setoran = `in`.readInt().toLong()
//    }
//
////    fun getNoRek(): String {
////        return noRek!!
////    }
////
////    fun setNoRek(noRek: String) {
////        this.noRek = noRek
////    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeString(this.noRek)
//        dest.writeString(this.nama)
//        dest.writeLong(this.saldoSimpanan)
//        dest.writeLong(this.saldoPinjaman)
//        dest.writeLong(this.angsuran)
//        dest.writeLong(this.tglTrans)
//        dest.writeLong(this.setoran)
//    }
//
//    companion object CREATOR : Creator<Rekening> {
//        override fun createFromParcel(parcel: Parcel): Rekening {
//            return Rekening(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Rekening?> {
//            return arrayOfNulls(size)
//        }
//    }