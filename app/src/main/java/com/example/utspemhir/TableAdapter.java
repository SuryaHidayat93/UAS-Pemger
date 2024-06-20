package com.example.utspemhir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private List<TableRow> tableRows;

    public TableAdapter(List<TableRow> tableRows) {
        this.tableRows = tableRows;
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        TableRow tableRow = tableRows.get(position);
        holder.surahTextView.setText(tableRow.getSurah());
        holder.tanggalTextView.setText(tableRow.getTanggal());
        holder.persyaratanTextView.setText(tableRow.getPersyaratan());
        holder.parafPATextView.setText(tableRow.getParafPA());
    }

    @Override
    public int getItemCount() {
        return tableRows.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {
        TextView surahTextView;
        TextView tanggalTextView;
        TextView persyaratanTextView;
        TextView parafPATextView;

        public TableViewHolder(View itemView) {
            super(itemView);
            surahTextView = itemView.findViewById(R.id.tv_surah);
            tanggalTextView = itemView.findViewById(R.id.tv_tanggal);
            persyaratanTextView = itemView.findViewById(R.id.tv_persyaratan);
            parafPATextView = itemView.findViewById(R.id.tv_paraf_pa);
        }
    }
}
