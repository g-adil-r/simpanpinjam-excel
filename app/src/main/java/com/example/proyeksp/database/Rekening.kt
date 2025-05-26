package com.example.proyeksp.database

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rekening",
    indices = [Index(name = "no_rek_index", value = ["no_rek"], unique = true)]
)
class Rekening : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "no_rek")
    var noRek: String? = null

    @ColumnInfo(name = "nama")
    var nama: String? = null

    @ColumnInfo(name = "saldo_simpanan")
    var saldoSimpanan: Long = 0

    @ColumnInfo(name = "saldo_pinjaman")
    var saldoPinjaman: Long = 0

    @ColumnInfo(name = "angsuran")
    var angsuran: Long = 0

    @ColumnInfo(name = "tgl_trans")
    var tglTrans: Long = 0

    @ColumnInfo(name = "setoran")
    var setoran: Long = 0

    constructor()

    constructor(
        noRek: String,
        nama: String?,
        saldoSimpanan: Long,
        saldoPinjaman: Long,
        angsuran: Long
    ) {
        this.noRek = noRek
        this.nama = nama
        this.saldoSimpanan = saldoSimpanan
        this.saldoPinjaman = saldoPinjaman
        this.angsuran = angsuran
        this.tglTrans = 0
        this.setoran = 0
    }

    protected constructor(`in`: Parcel) {
        noRek = `in`.readString()
        nama = `in`.readString()
        saldoSimpanan = `in`.readInt().toLong()
        saldoPinjaman = `in`.readInt().toLong()
        angsuran = `in`.readInt().toLong()
        tglTrans = `in`.readLong()
        setoran = `in`.readInt().toLong()
    }

//    fun getNoRek(): String {
//        return noRek!!
//    }
//
//    fun setNoRek(noRek: String) {
//        this.noRek = noRek
//    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.noRek)
        dest.writeString(this.nama)
        dest.writeLong(this.saldoSimpanan)
        dest.writeLong(this.saldoPinjaman)
        dest.writeLong(this.angsuran)
        dest.writeLong(this.tglTrans)
        dest.writeLong(this.setoran)
    }

    companion object CREATOR : Creator<Rekening> {
        override fun createFromParcel(parcel: Parcel): Rekening {
            return Rekening(parcel)
        }

        override fun newArray(size: Int): Array<Rekening?> {
            return arrayOfNulls(size)
        }
    }
}
