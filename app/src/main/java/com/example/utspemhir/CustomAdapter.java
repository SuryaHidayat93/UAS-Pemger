package com.example.utspemhir;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

            holder.itemView.setOnLongClickListener(v -> {
                SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String role = sharedPreferences.getString("user_role", null);

                if ("dosen".equals(role)) {
                    PopupMenu popup = new PopupMenu(context, holder.itemView);
                    popup.inflate(R.menu.popup_menu);
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.delete) {
                            String token = sharedPreferences.getString("jwt_token", null);
                            if (token != null) {
                                new DeleteSetoranTask(data.getIdSetoran(), position, token).execute();
                            } else {
                                Toast.makeText(context, "Token not found", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                } else {

                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private class DeleteSetoranTask extends AsyncTask<Void, Void, Boolean> {
        private int idSetoran;
        private int position;
        private String token;

        public DeleteSetoranTask(int idSetoran, int position, String token) {
            this.idSetoran = idSetoran;
            this.position = position;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Log.d("DeleteSetoranTask", "Deleting id_setoran: " + idSetoran);

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new AuthInterceptor(token))
                        .build();

                String url = "https://samatif.xyz/setoran/delete.php";
                String postData = "id_setoran=" + idSetoran;

                RequestBody body = RequestBody.create(postData, okhttp3.MediaType.parse("application/x-www-form-urlencoded"));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                Log.d("DeleteSetoranTask", "Response Code: " + response.code());

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("DeleteSetoranTask", "Response: " + responseBody);

                    return responseBody.contains("success");
                }

                return false;

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
