package com.example.utspemhir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utspemhir.R;

import java.util.Calendar;

public class InputSetoranActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    AutoCompleteTextView autoCompleteTxt, autoCompleteTxt2, autoCompleteTxt3, autoCompleteTxt4;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterItems2;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputsetoran);

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

        // Data for autocomplete
        String[] items = {"An-Naba’", "AN-Naazi’at", "'Abasa'", "At-Takwir", "Al-Infithar", "Al-Muthaffifin"};
        String[] items2 = {"Sangat Baik", "Baik", "Cukup", "Kurang"};

        // Initialize adapter with the data
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        adapterItems2 = new ArrayAdapter<>(this, R.layout.list_item, items2);

        // Set adapter to AutoCompleteTextView
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt2.setAdapter(adapterItems2);
        autoCompleteTxt3.setAdapter(adapterItems2);
        autoCompleteTxt4.setAdapter(adapterItems2);

        // Listener for item selection
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Handle item selection
                Toast.makeText(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        autoCompleteTxt2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Handle item selection
                Toast.makeText(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        initDatePicker();
        dateButton.setText(getTodaysDate());
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
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
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
        Intent intent = new Intent(InputSetoranActivity.this, SetoranDosenActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        logoutMenu(InputSetoranActivity.this);
    }

    private void logoutMenu(InputSetoranActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Start LoginActivity
                Intent intent = new Intent(InputSetoranActivity.this, LoginActivity.class);
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
