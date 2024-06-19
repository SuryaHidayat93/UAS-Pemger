package com.example.utspemhir;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RiwayatMahasiswaActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    List<DataModel> dataModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayatmahasiswadosen);

        String nama = getIntent().getStringExtra("nama");
        String nim = getIntent().getStringExtra("nim");

        if (nim != null && nim.startsWith("Nim: ")) {
            nim = nim.substring(5); // Menghapus "Nim: " dari awal string
        }

        Log.d("RiwayatMahasiswaDosen", "Received NIM:" + nim);

        drawerLayout = findViewById(R.id.drawer_layer);
        recyclerView = findViewById(R.id.recyclerView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataModelList = new ArrayList<>();
        adapter = new CustomAdapter(this, dataModelList); // Pass context here
        recyclerView.setAdapter(adapter);

        new FetchSetoranData().execute(nim);

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
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, BerandaDosenActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, SetoranDosenActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, RiwayatDosenActivity.class);
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

        @Override
        protected String doInBackground(String... params) {
            String nim = params[0];
            String urlString = "https://samatif.000webhostapp.com/setoran/by-nim.php?nim=122501";
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    return response.toString();
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
}
