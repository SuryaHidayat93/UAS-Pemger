package com.example.utspemhir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SetoranDosenActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setorandosen);

        drawerLayout = findViewById(R.id.drawer_layer);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        List<Item> data = new ArrayList<>();
        data.add(new Item("Nama: Farras Lathief","Nim: 12250111328", "Semester 4", R.drawable.gambar1));
        data.add(new Item("Nama: Mahasiswa 2","Nim: 12250111514", "Semester 4", R.drawable.gambar1));
        data.add(new Item("Nama: Mahasiswa 3","Nim: 12250120341","Semester 4", R.drawable.gambar1));
        data.add(new Item("Nama: Mahasiswa 4","Nim: 12250111514", "Semester 6",R.drawable.gambar1));
        data.add(new Item("Nama: Mahasiswa 5","Nim: 12250120341", "Semester 8",R.drawable.gambar1));

        MyAdapter adapter = new MyAdapter(this, data, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Dropdown (Spinner)
        Spinner spinner = findViewById(R.id.spinner);

        // Sample data
        String[] items = {"", "Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5"};

        // Create adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapter to Spinner
        spinner.setAdapter(spinnerAdapter);

        // Reaction to selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(SetoranDosenActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing selected
            }
        });
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void beranda(View view){
        Intent intent = new Intent(SetoranDosenActivity.this, BerandaDosenActivity.class);
        startActivity(intent);
    }

    public void setoran(View view){
        // No action needed as you are already in SetoranDosenActivity
    }

    public void riwayat(View view){
        Intent intent = new Intent(SetoranDosenActivity.this, RiwayatDosenActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        logoutMenu(SetoranDosenActivity.this);
    }

    private void logoutMenu(SetoranDosenActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Start LoginActivity
                Intent intent = new Intent(SetoranDosenActivity.this, LoginActivity.class);
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

    @Override
    public void onItemClick(Item item) {
        Intent intent = new Intent(SetoranDosenActivity.this, InputSetoranActivity.class);
        intent.putExtra("nama", item.getName());
        intent.putExtra("nim", item.getNim());
        startActivity(intent);
    }
}
