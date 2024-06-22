package com.example.utspemhir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatDosenActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<Item> data = new ArrayList<>();
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayatdosen);

        drawerLayout = findViewById(R.id.drawer_layer);
        recyclerView = findViewById(R.id.recyclerview);
        spinner = findViewById(R.id.spinner);

        adapter = new MyAdapter(this, data, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Inisialisasi Spinner
        initializeSpinner();

        // Ambil data dari endpoint
        fetchDataFromEndpoint();
    }

    private void initializeSpinner() {
        String[] items = {"", "Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(RiwayatDosenActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak ada yang dipilih
            }
        });
    }

    private void fetchDataFromEndpoint() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", ""); // Ambil token dari SharedPreferences

        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient("https://samatif-ml.preview-domain.com/", token).create(ApiService.class);
        Call<DosenResponse> call = apiService.getDosenByNip("19981");

        call.enqueue(new Callback<DosenResponse>() {
            @Override
            public void onResponse(Call<DosenResponse> call, Response<DosenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Dosen> dosenList = response.body().getDosen();
                    if (!dosenList.isEmpty()) {
                        List<Student> mahasiswaList = dosenList.get(0).getMahasiswa();
                        data.clear();
                        for (Student student : mahasiswaList) {
                            data.add(new Item("Nama: " + student.getNama(), "Nim: " + student.getNIM(), "Semester " + student.getSemester(), R.drawable.gambar1));
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(RiwayatDosenActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DosenResponse> call, Throwable t) {
                Toast.makeText(RiwayatDosenActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(RiwayatDosenActivity.this, BerandaDosenActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        Intent intent = new Intent(RiwayatDosenActivity.this, SetoranDosenActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        // Tidak ada tindakan karena Anda sudah berada di RiwayatDosenActivity
    }

    public void logout(View view) {
        logoutMenu(RiwayatDosenActivity.this);
    }

    private void logoutMenu(RiwayatDosenActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(RiwayatDosenActivity.this, LoginActivity.class);
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

    @Override
    public void onItemClick(Item item) {
        Intent intent = new Intent(RiwayatDosenActivity.this, RiwayatMahasiswaDosenActivity.class);
        intent.putExtra("nama", item.getName());
        intent.putExtra("nim", item.getNim());
        startActivity(intent);
    }
}
