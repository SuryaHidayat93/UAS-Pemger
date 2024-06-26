package com.example.utspemhir;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;

    private List<DataModel> dataList;
    private Context context;

    public CustomAdapter(Context context, List<DataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            DataModel data = dataList.get(position);
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
        return dataList.size();
    }

    private class DeleteSetoranTask extends AsyncTask<Void, Void, Boolean> {
        private int idSetoran;
        private int position;

        public DeleteSetoranTask(int idSetoran, int position) {
            this.idSetoran = idSetoran;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Log.d("DeleteSetoranTask", "Deleting id_setoran: " + idSetoran); // Log the id_setoran

                URL url = new URL("https://samatif.000webhostapp.com/setoran/delete.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "id_setoran=" + idSetoran;
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d("DeleteSetoranTask", "Response Code: " + responseCode); // Log the response code

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("DeleteSetoranTask", "Response: " + response.toString()); // Log the response

                return response.toString().contains("success"); // Assuming the server responds with "success" on successful deletion

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                dataList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Setoran berhasil dihapus", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gagal menghapus setoran", Toast.LENGTH_SHORT).show();
            }
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
