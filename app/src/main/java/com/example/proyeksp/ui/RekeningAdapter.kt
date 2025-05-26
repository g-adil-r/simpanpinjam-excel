package com.example.proyeksp.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyeksp.R
import com.example.proyeksp.database.Rekening
import com.example.proyeksp.helper.CurrencyHelper
import com.example.proyeksp.ui.RekeningAdapter.RekeningViewHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RekeningAdapter(private val context: Context, private val rekeningList: List<Rekening?>?) :
    RecyclerView.Adapter<RekeningViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val dateFormat: DateFormat =
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RekeningViewHolder {
        val view = mInflater.inflate(R.layout.setoran_item, parent, false)
        return RekeningViewHolder(view)
    }

    override fun onBindViewHolder(holder: RekeningViewHolder, position: Int) {
        val current = rekeningList?.get(position)
        val longDate = current?.tglTrans

        holder.tvNama.text = current?.nama
        holder.tvNoRek.text = current?.noRek

        if (longDate == 0L) {
            holder.tvTgl.setTextColor(Color.RED)
            holder.tvTgl.text = context.resources.getText(R.string.no_date_setor)
            holder.tvSetor.text = "-"
        } else {
            if (current != null) {
                holder.tvSetor.text = CurrencyHelper.format(current.setoran)
            }
            holder.tvTgl.setTextColor(Color.rgb(34, 177, 76))
            holder.tvTgl.text = dateFormat.format(longDate?.let { Date(it) })
        }
    }

    override fun getItemCount(): Int {
        return rekeningList!!.size
    }

    class RekeningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNama: TextView = itemView.findViewById(R.id.setor_item_nama)
        var tvNoRek: TextView = itemView.findViewById(R.id.setor_item_norek)
        var tvSetor: TextView = itemView.findViewById(R.id.setor_item_setoran)
        var tvTgl: TextView = itemView.findViewById(R.id.setor_tgl_transaksi)
    }
}
