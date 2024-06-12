package com.example.utspemhir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BerandaDosenActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView namaUserTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berandadosen);

        drawerLayout = findViewById(R.id.drawer_layer);

        // Mengakses TextView dari sidebardosen.xml
        View sidebarView = findViewById(R.id.namauser);  // Pastikan ID ini sesuai dengan layout sidebardosen.xml
        namaUserTextView = sidebarView.findViewById(R.id.namauser);

        // Get the username from the intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");

        // Set the username to the TextView
        if (username != null) {
            namaUserTextView.setText(username);
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
                // Start LoginActivity
                Intent intent = new Intent(BerandaDosenActivity.this, LoginActivity.class);
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
}
