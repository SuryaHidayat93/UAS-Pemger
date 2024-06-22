package com.example.utspemhir;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InputSetoranActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    AutoCompleteTextView autoCompleteTxt, autoCompleteTxt2, autoCompleteTxt3, autoCompleteTxt4;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterItems2;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private List<Surah> surahList;
    private String token;
    private String nip;  // Tambahkan variabel nip

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputsetoran);

        // Ambil token dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", "");
        nip = sharedPreferences.getString("nip", "");  // Ambil nip dari SharedPreferences

        String nama = getIntent().getStringExtra("nama");
        String nim = getIntent().getStringExtra("nim");

        TextView namaTextView = findViewById(R.id.nama);
        TextView nimTextView = findViewById(R.id.nim);
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        autoCompleteTxt2 = findViewById(R.id.auto_complete_txt2);
        autoCompleteTxt3 = findViewById(R.id.auto_complete_txt3);
        autoCompleteTxt4 = findViewById(R.id.auto_complete_txt4);
        dateButton = findViewById(R.id.datePickerButton);

        namaTextView.setText(nama);
        nimTextView.setText(nim);

        drawerLayout = findViewById(R.id.drawer_layer);

        // Data for other autocomplete fields
        String[] items2 = {"Sangat Baik", "Baik", "Cukup", "Kurang"};
        adapterItems2 = new ArrayAdapter<>(this, R.layout.list_item, items2);

        autoCompleteTxt2.setAdapter(adapterItems2);
        autoCompleteTxt3.setAdapter(adapterItems2);
        autoCompleteTxt4.setAdapter(adapterItems2);

        // Listener for item selection
        autoCompleteTxt2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompleteTxt3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompleteTxt4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch data from API
        fetchSurahData();

        initDatePicker();
        dateButton.setText(getTodaysDate());
    }

    private void fetchSurahData() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://samatif.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Surah>> call = apiService.getAllSurah();

        call.enqueue(new Callback<List<Surah>>() {
            @Override
            public void onResponse(Call<List<Surah>> call, Response<List<Surah>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    surahList = response.body();
                    String[] surahNames = new String[surahList.size()];

                    for (int i = 0; i < surahList.size(); i++) {
                        surahNames[i] = surahList.get(i).getName();
                    }

                    adapterItems = new ArrayAdapter<>(InputSetoranActivity.this, R.layout.list_item, surahNames);
                    autoCompleteTxt.setAdapter(adapterItems);

                    autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String item = parent.getItemAtPosition(position).toString();
                            Toast.makeText(getApplicationContext(), "Surah: " + item, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    showToast("Gagal mengambil data Surah");
                }
            }

            @Override
            public void onFailure(Call<List<Surah>> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void beranda(View view) {
        Intent intent = new Intent(InputSetoranActivity.this, BerandaDosenActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        Intent intent = new Intent(InputSetoranActivity.this, SetoranDosenActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        Intent intent = new Intent(InputSetoranActivity.this, RiwayatDosenActivity.class);
        startActivity(intent);
    }

    public void SimpanButtonClick(View view) {
        // Mengambil data nim dari Intent dan menghapus teks tambahan jika ada
        String nim = getIntent().getStringExtra("nim");
        if (nim.contains(":")) {
            nim = nim.split(":")[1].trim();
        }

        // nip sudah diambil dari SharedPreferences saat onCreate
        int idSurah = getSelectedSurahId();
        String tanggal = dateButton.getText().toString();
        String kelancaran = autoCompleteTxt2.getText().toString();
        String tajwid = autoCompleteTxt3.getText().toString();
        String makhrajulHuruf = autoCompleteTxt4.getText().toString();

        Log.d("SimpanButtonClick", "nim: " + nim);
        Log.d("SimpanButtonClick", "nip: " + nip);
        Log.d("SimpanButtonClick", "idSurah: " + idSurah);
        Log.d("SimpanButtonClick", "tanggal: " + tanggal);
        Log.d("SimpanButtonClick", "kelancaran: " + kelancaran);
        Log.d("SimpanButtonClick", "tajwid: " + tajwid);
        Log.d("SimpanButtonClick", "makhrajulHuruf: " + makhrajulHuruf);

        if (nim != null && !nip.isEmpty() && idSurah != -1 && !tanggal.isEmpty() && !kelancaran.isEmpty() && !tajwid.isEmpty() && !makhrajulHuruf.isEmpty()) {
            submitSetoran(nim, nip, idSurah, tanggal, kelancaran, tajwid, makhrajulHuruf);
        } else {
            showToast("Semua field harus diisi.");
        }
    }

    private int getSelectedSurahId() {
        String selectedSurah = autoCompleteTxt.getText().toString();

        // Cari Surah yang sesuai dengan nama yang dipilih
        for (Surah surah : surahList) {
            if (surah.getName().equals(selectedSurah)) {
                return surah.getId();
            }
        }

        // Jika tidak ditemukan, kembalikan -1 atau berikan notifikasi bahwa Surah tidak ditemukan
        showToast("Surah tidak ditemukan");
        return -1;
    }

    private void submitSetoran(String nim, String nip, int idSurah, String tanggal, String kelancaran, String tajwid, String makhrajulHuruf) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://samatif.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<SetoranResponse> call = apiService.insertSetoran(nim, nip, idSurah, tanggal, kelancaran, tajwid, makhrajulHuruf);

        call.enqueue(new Callback<SetoranResponse>() {
            @Override
            public void onResponse(Call<SetoranResponse> call, Response<SetoranResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SetoranResponse apiResponse = response.body();
                    showToast(apiResponse.getMessage());
                } else {
                    showToast("Gagal mengirim data setoran");
                }
            }

            @Override
            public void onFailure(Call<SetoranResponse> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
