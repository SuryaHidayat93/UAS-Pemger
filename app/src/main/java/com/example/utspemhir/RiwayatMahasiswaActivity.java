package com.example.utspemhir;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class RiwayatMahasiswaActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayatmahasiswa);

        drawerLayout = findViewById(R.id.drawer_layer);
        progressBar = findViewById(R.id.progressBar);

        // Panggil showProgressBar() untuk menetapkan kemajuan menjadi 25%
        showProgressBar();
    }


    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void beranda(View view) {
        showProgressBar(); // Tampilkan progress bar sebelum memulai aktivitas beranda
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, BerandaMahasiswaActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        showProgressBar(); // Tampilkan progress bar sebelum memulai aktivitas setoran
        Intent intent = new Intent(RiwayatMahasiswaActivity.this, SetoranMahasiswaActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        // Tidak perlu menampilkan progress bar karena tetap berada di halaman ini
    }

    public void logout(View view) {
        logoutMenu();
    }

    private void logoutMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Tampilkan progress bar saat melakukan logout
                showProgressBar();
                // Start LoginActivity
                Intent intent = new Intent(RiwayatMahasiswaActivity.this, LoginActivity.class);
                startActivity(intent);
                // Finish current activity
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

    private void showProgressBar() {
        // Menetapkan kemajuan progress bar menjadi 25% dari nilai maksimum (100)
        progressBar.setProgress(25);
        // Menampilkan progress bar
        progressBar.setVisibility(View.VISIBLE);
    }


}
