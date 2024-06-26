package com.example.utspemhir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BerandaDosenActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView namaUserTextView;
    TextView namaDosenTextView;
    TextView nipTextView;
    TextView dosenNameTextView; // Tambahkan ini
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berandadosen);

        drawerLayout = findViewById(R.id.drawer_layer);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        View sidebarView = findViewById(R.id.sidebardosen);
        namaDosenTextView = sidebarView.findViewById(R.id.namadosen);
        nipTextView = sidebarView.findViewById(R.id.nip);
        dosenNameTextView = findViewById(R.id.dosen_name); // Inisialisasi TextView dosen_name

        String token = sharedPreferences.getString("jwt_token", "");
        if (!token.isEmpty()) {
            fetchUserData(token);
        } else {
            // Jika token kosong, ambil nama dari SharedPreferences
            String nama = sharedPreferences.getString("nama", "Nama Dosen");
            dosenNameTextView.setText(nama);
        }
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void beranda(View view) {
        Intent intent = new Intent(BerandaDosenActivity.this, BerandaDosenActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        Intent intent = new Intent(BerandaDosenActivity.this, SetoranDosenActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        Intent intent = new Intent(BerandaDosenActivity.this, RiwayatDosenActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        logoutMenu(BerandaDosenActivity.this);
    }

    private void logoutMenu(BerandaDosenActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(BerandaDosenActivity.this, LoginActivity.class);
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

    private void fetchUserData(String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://samatif.xyz/login.php?action=get");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", "Bearer " + token);

                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    inputStream.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String nama = jsonObject.getString("nama");
                    String nip = jsonObject.getString("nip");

                    // Simpan NIP dan nama ke SharedPreferences
                    saveUserDataToSharedPreferences(nama, nip);

                    // Tampilkan di log
                    Log.d("UserData", "Nama: " + nama + ", NIP: " + nip);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            namaDosenTextView.setText(nama);
                            nipTextView.setText(nip);
                            dosenNameTextView.setText(nama); // Tampilkan nama di TextView dosen_name
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("FetchUserData", "Exception: " + e.getMessage());
                }
            }
        }).start();
    }

    private void saveUserDataToSharedPreferences(String nama, String nip) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nama", nama);
        editor.putString("nip", nip);
        editor.apply();
    }
}
