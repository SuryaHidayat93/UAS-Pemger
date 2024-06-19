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

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        TableRow tableRow = tableRows.get(position);
        holder.surah.setText(tableRow.getSurah());
        holder.tanggal.setText(tableRow.getTanggal());
        holder.persyaratan.setText(tableRow.getPersyaratan());
        holder.parafPA.setText(tableRow.getParafPA());
    }

    @Override
    public int getItemCount() {
        return tableRows.size();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {

        TextView surah, tanggal, persyaratan, parafPA;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            surah = itemView.findViewById(R.id.tv_surah);
            tanggal = itemView.findViewById(R.id.tv_tanggal);
            persyaratan = itemView.findViewById(R.id.tv_persyaratan);
            parafPA = itemView.findViewById(R.id.tv_paraf_pa);
        }
    }
}
