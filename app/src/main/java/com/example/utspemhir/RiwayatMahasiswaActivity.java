package com.example.utspemhir;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RiwayatMahasiswaActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    List<DataModel> dataModelList;
    TextView namaUserTextView;
    TextView nimTextView;
    TextView semesterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayatmahasiswa);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nim = sharedPreferences.getString("nim", "");
        String token = sharedPreferences.getString("jwt_token", ""); // Retrieve token from SharedPreferences

        new FetchMahasiswaDataTask(token).execute(nim);

        View sidebarView = findViewById(R.id.sidebarmahasiswa);
        namaUserTextView = sidebarView.findViewById(R.id.namamahasiswa);
        nimTextView = sidebarView.findViewById(R.id.nim);
        semesterTextView = sidebarView.findViewById(R.id.semester);

        Log.d("RiwayatMahasiswaDosen", "Received NIM:" + nim);

        drawerLayout = findViewById(R.id.drawer_layer);
        recyclerView = findViewById(R.id.recyclerView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataModelList = new ArrayList<>();
        adapter = new CustomAdapter(this, dataModelList); // Pass context here
        recyclerView.setAdapter(adapter);

        new FetchSetoranData(token).execute(nim);

        // Set up button click listener
        Button buttonProgress = findViewById(R.id.buttonprogress);
        String finalNim = nim;
        buttonProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiwayatMahasiswaActivity.this, ActivityProgress.class);
                intent.putExtra("nim", finalNim);  // Mengirimkan NIM ke ActivityProgress
                startActivity(intent);
            }
        });
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void beranda(View view) {
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, BerandaMahasiswaActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, SetoranMahasiswaActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, RiwayatMahasiswaActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        logoutMenu(RiwayatMahasiswaActivity.this);
    }

    private void logoutMenu(RiwayatMahasiswaActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(RiwayatMahasiswaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private class FetchSetoranData extends AsyncTask<String, Void, String> {

        private String token;

        public FetchSetoranData(String token) {
            this.token = token;
        }


        @Override
        protected String doInBackground(String... params) {
            String nim = params[0];
            String urlString = "https://samatif.xyz/setoran/by-nim.php?nim=" + nim;

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(token))
                    .build();

            Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            // Log the URL and headers
            Log.d("FetchSetoranData", "Request URL: " + urlString);
            Log.d("FetchSetoranData", "Request Headers: " + request.headers().toString());

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("FetchSetoranData", "Server Response: " + result);  // Tambahkan log ini

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray setoranArray = jsonObject.getJSONArray("setoran");

                    for (int i = 0; i < setoranArray.length(); i++) {
                        JSONObject setoranObject = setoranArray.getJSONObject(i);

                        int idSetoran = setoranObject.getInt("id_setoran");
                        String tanggal = setoranObject.getString("tanggal");
                        String surah = setoranObject.getString("nama_surah");
                        String kelancaran = setoranObject.getString("kelancaran");
                        String tajwid = setoranObject.getString("tajwid");
                        String makhrajulHuruf = setoranObject.getString("makhrajul_huruf");

                        DataModel dataModel = new DataModel(idSetoran, tanggal, surah, kelancaran, tajwid, makhrajulHuruf);
                        dataModelList.add(dataModel);
                    }

                    // Notify adapter about the data change
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the error
            }
        }
    }

    private class FetchMahasiswaDataTask extends AsyncTask<String, Void, String> {

        private String token;

        public FetchMahasiswaDataTask(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(String... params) {
            String nim = params[0];
            String urlString = "https://samatif.xyz/mahasiswa/by-nim.php?nim=" + nim;

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(token))
                    .build();

            Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    Log.e("FetchMahasiswaDataTask", "Failed to fetch data. Response code: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("FetchMahasiswaDataTask", "Server Response: " + result);  // Tambahkan log ini

                try {
                    JSONArray jsonArray = new JSONArray(result); // Parse as JSONArray

                    // Assuming you only need the first object if multiple are returned
                    JSONObject jsonObject = jsonArray.getJSONObject(0); // Get the first object

                    String nama = jsonObject.getString("Nama"); // "Nama" is the key based on your example response
                    String nim = jsonObject.getString("NIM");
                    String semester = jsonObject.getString("Semester");

                    // Set TextViews with retrieved data
                    namaUserTextView.setText(nama);
                    nimTextView.setText(nim);
                    semesterTextView.setText("semester: " + semester);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("FetchMahasiswaDataTask", "Failed to fetch data.");
            }
        }
    }
}
