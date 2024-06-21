package com.example.utspemhir;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetoranMahasiswaActivity extends AppCompatActivity {

    private static final String TAG = "SetoranMahasiswaActivity";
    RecyclerView recyclerView;
    TableAdapter tableAdapter;
    List<TableRow> tableRows;
    Map<String, String> surahDates;
    DrawerLayout drawerLayout;
    TextView namaUserTextView;
    TextView nimTextView;
    TextView semesterTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setoranmahasiswa);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tableRows = new ArrayList<>();
        tableAdapter = new TableAdapter(tableRows);
        recyclerView.setAdapter(tableAdapter);
        surahDates = new HashMap<>();

        drawerLayout = findViewById(R.id.drawer_layer);

        // Access TextViews from sidebarmahasiswa.xml
        View sidebarView = findViewById(R.id.sidebarmahasiswa);
        namaUserTextView = sidebarView.findViewById(R.id.namamahasiswa);
        nimTextView = sidebarView.findViewById(R.id.nim);
        semesterTextView = sidebarView.findViewById(R.id.semester);

        // Get NIM from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nim = sharedPreferences.getString("nim", "");

        if (nim.isEmpty()) {
            Log.w(TAG, "NIM is empty or not available in SharedPreferences");
        } else {
            Log.d(TAG, "NIM from SharedPreferences: " + nim);
            fetchSurahData(nim);
            fetchSetoranData(nim);
            new FetchMahasiswaDataTask().execute(nim);
        }
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void beranda(View view) {
        Intent intent = new Intent(SetoranMahasiswaActivity.this, BerandaMahasiswaActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        Intent intent = new Intent(SetoranMahasiswaActivity.this, SetoranMahasiswaActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        Intent intent = new Intent(SetoranMahasiswaActivity.this, RiwayatMahasiswaActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        logoutMenu(SetoranMahasiswaActivity.this);
    }

    private void logoutMenu(SetoranMahasiswaActivity mainActivity) {
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

                Intent intent = new Intent(SetoranMahasiswaActivity.this, LoginActivity.class);
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

    private void fetchSurahData(String nim) {
        ApiService apiService = RetrofitClient.getClient("https://samatif-ml.preview-domain.com/").create(ApiService.class);
        Call<SurahResponse> call = apiService.getSurahDetails(nim);

        call.enqueue(new Callback<SurahResponse>() {
            @Override
            public void onResponse(Call<SurahResponse> call, Response<SurahResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SurahResponse surahResponse = response.body();
                    Log.d(TAG, "Fetched Surah Data: " + surahResponse.getPercentages());
                    updateTableRows(surahResponse);
                } else {
                    Log.e(TAG, "Failed to fetch surah data, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SurahResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch surah data", t);
            }
        });
    }

    private void fetchSetoranData(String nim) {
        ApiService apiService = RetrofitClient.getClient("https://samatif-ml.preview-domain.com/").create(ApiService.class);
        Call<SetoranResponse> call = apiService.getSetoranDetails(nim);

        call.enqueue(new Callback<SetoranResponse>() {
            @Override
            public void onResponse(Call<SetoranResponse> call, Response<SetoranResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SetoranResponse setoranResponse = response.body();
                    Log.d(TAG, "Fetched Setoran Data: " + setoranResponse.getSetoran());
                    updateSurahDates(setoranResponse);
                } else {
                    Log.e(TAG, "Failed to fetch setoran data, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SetoranResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch setoran data", t);
            }
        });
    }

    private void updateSurahDates(SetoranResponse setoranResponse) {
        Log.d(TAG, "Updating surah dates with fetched data");

        // Get data tanggal from setoranResponse
        if (setoranResponse.getSetoran() != null) {
            for (SetoranResponse.Setoran setoran : setoranResponse.getSetoran()) {
                surahDates.put(setoran.getNamaSurah(), setoran.getTanggal());
            }
        } else {
            Log.w(TAG, "Setoran data is null");
        }

        // Update tanggal on existing tableRows
        for (TableRow row : tableRows) {
            String date = surahDates.getOrDefault(row.getSurah(), "");
            row.setTanggal(date);
        }

        tableAdapter.notifyDataSetChanged();
        Log.d(TAG, "Surah dates updated successfully");
    }

    private void updateTableRows(SurahResponse surahResponse) {
        Log.d(TAG, "Updating table rows with fetched data");

        // Update table rows with data from surahResponse
        if (surahResponse.getPercentages() != null && !surahResponse.getPercentages().isEmpty()) {
            for (SurahResponse.Percentage percentage : surahResponse.getPercentages()) {
                List<String> surahNames = percentage.getSurahNames();
                String syarat = percentage.getLang();
                if (surahNames != null) {
                    for (String surahName : surahNames) {
                        String date = surahDates.getOrDefault(surahName, "");
                        tableRows.add(new TableRow(surahName, date, syarat, ""));
                    }
                }
            }
        }

        tableAdapter.notifyDataSetChanged();
        Log.d(TAG, "Table rows updated successfully");
    }

    private class FetchMahasiswaDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String nim = params[0];
            String urlString = "https://samatif-ml.preview-domain.com/mahasiswa/by-nim.php?nim=" + nim;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    Log.e("FetchMahasiswaDataTask", "Failed to fetch data. Response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("FetchMahasiswaDataTask", "Response: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String nama = jsonObject.getString("nama");
                    String nim = jsonObject.getString("nim");
                    String semester = jsonObject.getString("semester");

                    namaUserTextView.setText(nama);
                    nimTextView.setText(nim);
                    semesterTextView.setText(semester);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("FetchMahasiswaDataTask", "Failed to fetch data.");
            }
        }
    }
}
