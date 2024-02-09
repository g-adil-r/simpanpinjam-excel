package com.example.proyeksp.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "rekening", indices = {
        @Index(name = "no_rek_index", value = "no_rek", unique = true)
})
public class Rekening implements Parcelable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "no_rek")
    private String noRek;

    @ColumnInfo (name = "nama")
    private String nama;

    @ColumnInfo (name = "saldo_simpanan")
    private long saldoSimpanan;

    @ColumnInfo (name = "saldo_pinjaman")
    private long saldoPinjaman;

    @ColumnInfo (name = "angsuran")
    private long angsuran;

    @ColumnInfo (name = "tgl_trans")
    private long tglTrans;

    @ColumnInfo (name = "setoran")
    private long setoran;

    public Rekening() {
    }

    public Rekening(@NonNull String noRek, String nama, long saldoSimpanan, long saldoPinjaman, long angsuran) {
        this.noRek = noRek;
        this.nama = nama;
        this.saldoSimpanan = saldoSimpanan;
        this.saldoPinjaman = saldoPinjaman;
        this.angsuran = angsuran;
        this.tglTrans = 0;
        this.setoran = 0;
    }

    protected Rekening(Parcel in) {
        noRek = in.readString();
        nama = in.readString();
        saldoSimpanan = in.readInt();
        saldoPinjaman = in.readInt();
        angsuran = in.readInt();
        tglTrans = in.readLong();
        setoran = in.readInt();
    }

    public static final Creator<Rekening> CREATOR = new Creator<Rekening>() {
        @Override
        public Rekening createFromParcel(Parcel in) {
            return new Rekening(in);
        }

        @Override
        public Rekening[] newArray(int size) {
            return new Rekening[size];
        }
    };

    @NonNull
    public String getNoRek() {
        return noRek;
    }

    public void setNoRek(@NonNull String noRek) {
        this.noRek = noRek;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public long getSaldoSimpanan() {
        return saldoSimpanan;
    }

    public void setSaldoSimpanan(long saldoSimpanan) {
        this.saldoSimpanan = saldoSimpanan;
    }

    public long getSaldoPinjaman() {
        return saldoPinjaman;
    }

    public void setSaldoPinjaman(long saldoPinjaman) {
        this.saldoPinjaman = saldoPinjaman;
    }

    public long getAngsuran() {
        return angsuran;
    }

    public void setAngsuran(long angsuran) {
        this.angsuran = angsuran;
    }

    public long getTglTrans() {
        return tglTrans;
    }

    public void setTglTrans(long tglTrans) {
        this.tglTrans = tglTrans;
    }

    public long getSetoran() {
        return setoran;
    }

    public void setSetoran(long setoran) {
        this.setoran = setoran;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.noRek);
        dest.writeString(this.nama);
        dest.writeLong(this.saldoSimpanan);
        dest.writeLong(this.saldoPinjaman);
        dest.writeLong(this.angsuran);
        dest.writeLong(this.tglTrans);
        dest.writeLong(this.setoran);
    }
}
