package com.example.utspemhir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<DataModel> dataList;
    private Context context;

    public CustomAdapter(List<DataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_table, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_layout, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            DataModel data = dataList.get(position - 1); // Adjust position to account for header
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            holder.textViewTanggal.setText(data.getTanggal());
            holder.textViewSurah.setText(data.getSurah());
            holder.textViewKelancaran.setText(data.getKelancaran());
            holder.textViewTajwid.setText(data.getTajwid());
            holder.textViewMakhrajulHuruf.setText(data.getMakhrajulHuruf());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1; // Add 1 for the header
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // Define header views here

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize header views
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTanggal, textViewSurah, textViewKelancaran, textViewTajwid, textViewMakhrajulHuruf;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTanggal = itemView.findViewById(R.id.textViewTanggal);
            textViewSurah = itemView.findViewById(R.id.textViewSurah);
            textViewKelancaran = itemView.findViewById(R.id.textViewKelancaran);
            textViewTajwid = itemView.findViewById(R.id.textViewTajwid);
            textViewMakhrajulHuruf = itemView.findViewById(R.id.textViewMakhrajulHuruf);
        }
    }
}
