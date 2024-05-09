package com.example.proyeksp.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyeksp.R;
import com.example.proyeksp.database.Rekening;
import com.example.proyeksp.helper.CurrencyHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RekeningAdapter extends RecyclerView.Adapter<RekeningAdapter.RekeningViewHolder> {
    private final LayoutInflater mInflater;
    private List<Rekening> rekeningList;
    private final DateFormat dateFormat;
    private final Context context;

    public RekeningAdapter(Context context, List<Rekening> rekenings) {
        this.rekeningList = rekenings;
        this.context = context;

        dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RekeningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.setoran_item,parent,false);
        return new RekeningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RekeningViewHolder holder, int position) {
        Rekening current = rekeningList.get(position);
        long longDate = current.getTglTrans();

        holder.tvNama.setText(current.getNama());
        holder.tvNoRek.setText(current.getNoRek());

        if (longDate == 0) {
            holder.tvTgl.setTextColor(Color.RED);
            holder.tvTgl.setText(context.getResources().getText(R.string.no_date_setor));
            holder.tvSetor.setText("-");
        } else {
            holder.tvSetor.setText(CurrencyHelper.format(current.getSetoran()));
            holder.tvTgl.setTextColor(Color.rgb(34, 177, 76));
            holder.tvTgl.setText(dateFormat.format(new Date(longDate)));
        }
    }

    @Override
    public int getItemCount() {
        return rekeningList.size();
    }

    static class RekeningViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvNoRek, tvSetor, tvTgl;
        public RekeningViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.setor_item_nama);
            tvNoRek = itemView.findViewById(R.id.setor_item_norek);
            tvSetor = itemView.findViewById(R.id.setor_item_setoran);
            tvTgl = itemView.findViewById(R.id.setor_tgl_transaksi);
        }
    }
}
