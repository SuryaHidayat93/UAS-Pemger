package com.example.utspemhir;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetoranMahasiswaActivity extends AppCompatActivity {

    private static final String TAG = "SetoranMahasiswaActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    TableAdapter tableAdapter;
    List<TableRow> tableRows;
    TextView downloadButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setoranmahasiswa);

        drawerLayout = findViewById(R.id.drawer_layer);
        recyclerView = findViewById(R.id.recycler_view);
        downloadButton = findViewById(R.id.download);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tableRows = new ArrayList<>();
        tableAdapter = new TableAdapter(tableRows);
        recyclerView.setAdapter(tableAdapter);

        // Mendapatkan token dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", "");
        String nim = sharedPreferences.getString("nim", "");

        if (token.isEmpty()) {
            Log.w(TAG, "Token is empty or not available in SharedPreferences");
            // Lakukan sesuatu jika token kosong, misalnya kembali ke halaman login
            // atau tampilkan pesan kepada pengguna bahwa mereka perlu login kembali.
        } else if (nim.isEmpty()) {
            Log.w(TAG, "NIM is empty or not available in SharedPreferences");
            // Lakukan sesuatu jika NIM kosong, misalnya kembali ke halaman login
            // atau tampilkan pesan kepada pengguna bahwa mereka perlu login kembali.
        } else {
            Log.d(TAG, "Token from SharedPreferences: " + token);
            Log.d(TAG, "NIM from SharedPreferences: " + nim);
            // Setelah mendapatkan NIM, lakukan fetchSurahData
            fetchSurahData(nim);

            // Set download button click listener
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkPermission()) {
                        Bitmap recyclerViewScreenshot = getRecyclerViewScreenshot(recyclerView);
                        saveBitmapAsPDF(recyclerViewScreenshot);
                    } else {
                        requestPermission();
                    }
                }
            });
        }
    }

    private Bitmap getRecyclerViewScreenshot(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        if (adapter == null) {
            Log.e(TAG, "Adapter is null");
            return null;
        }

        int itemCount = adapter.getItemCount();
        int totalHeight = 0;
        int totalWidth = view.getWidth();

        // Inflate the header view
        View headerView = getLayoutInflater().inflate(R.layout.table_header, null);
        headerView.measure(
                View.MeasureSpec.makeMeasureSpec(totalWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        headerView.layout(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight());
        headerView.setDrawingCacheEnabled(true);
        headerView.buildDrawingCache();
        Bitmap headerBitmap = Bitmap.createBitmap(headerView.getDrawingCache());
        headerView.setDrawingCacheEnabled(false);

        totalHeight += headerView.getMeasuredHeight();

        List<Bitmap> bitmaps = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(totalWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            bitmaps.add(Bitmap.createBitmap(holder.itemView.getDrawingCache()));
            holder.itemView.setDrawingCacheEnabled(false);

            totalHeight += holder.itemView.getMeasuredHeight();
        }

        Bitmap bigBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        Paint paint = new Paint();
        int heightOffset = 0;

        // Draw the header bitmap on the big canvas
        bigCanvas.drawBitmap(headerBitmap, 0f, heightOffset, paint);
        heightOffset += headerBitmap.getHeight();
        headerBitmap.recycle();

        for (Bitmap bitmap : bitmaps) {
            bigCanvas.drawBitmap(bitmap, 0f, heightOffset, paint);
            heightOffset += bitmap.getHeight();
            bitmap.recycle();
        }

        return bigBitmap;
    }

    private void saveBitmapAsPDF(Bitmap bitmap) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/Download/";
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = directoryPath + "RecyclerViewScreenshot.pdf";
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "PDF Saved to " + filePath, Toast.LENGTH_LONG).show();
            Log.d(TAG, "PDF saved successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to save PDF: " + e.getMessage());
        }

        document.close();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void fetchSurahData(String nim) {
        Log.d(TAG, "Fetching Surah data...");
        // Lakukan sesuatu dengan NIM, misalnya pass ke API untuk fetch data Surah
        ApiService apiService = RetrofitClient.getClient("https://samatif.000webhostapp.com/").create(ApiService.class);
        apiService.getSurahDetails(nim).enqueue(new Callback<SurahResponse>() {
            @Override
            public void onResponse(Call<SurahResponse> call, Response<SurahResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SurahResponse surahResponse = response.body();
                    Log.d(TAG, "Surah data fetched successfully");
                    fetchSetoranData(surahResponse);
                } else {
                    String errorMessage = "Failed to fetch Surah data: ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        errorMessage += "Response body is null";
                    }
                    Toast.makeText(SetoranMahasiswaActivity.this, "Gagal mengambil data surah", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<SurahResponse> call, Throwable t) {
                Toast.makeText(SetoranMahasiswaActivity.this, "Gagal mengambil data surah: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching Surah data: " + t.getMessage());
            }
        });
    }

    private void fetchSetoranData(SurahResponse surahResponse) {
        Log.d(TAG, "Fetching Setoran data...");
        // Ambil NIM dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nim = sharedPreferences.getString("nim", "");

        ApiService apiService = RetrofitClient.getClient("https://samatif.000webhostapp.com/").create(ApiService.class);
        apiService.getSetoranDetails(nim).enqueue(new Callback<SetoranResponse>() {
            @Override
            public void onResponse(Call<SetoranResponse> call, Response<SetoranResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SetoranResponse setoranResponse = response.body();
                    Log.d(TAG, "Setoran data fetched successfully");
                    updateTableRows(surahResponse, setoranResponse);
                } else {
                    String errorMessage = "Failed to fetch Setoran data: ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        errorMessage += "Response body is null";
                    }
                    Toast.makeText(SetoranMahasiswaActivity.this, "Gagal mengambil data setoran", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<SetoranResponse> call, Throwable t) {
                Toast.makeText(SetoranMahasiswaActivity.this, "Gagal mengambil data setoran: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching Setoran data: " + t.getMessage());
            }
        });
    }

    private void updateTableRows(SurahResponse surahResponse, SetoranResponse setoranResponse) {
        Log.d(TAG, "Updating table rows with fetched data");
        tableRows.clear();
        Map<String, String> surahDates = new HashMap<>();

        // Ambil data tanggal dari setoranResponse
        for (SetoranResponse.Setoran setoran : setoranResponse.getSetoran()) {
            surahDates.put(setoran.getNamaSurah(), setoran.getTanggal());
        }

        // Update baris tabel dengan data dari surahResponse dan setoranResponse
        for (SurahResponse.Percentage percentage : surahResponse.getPercentages()) {
            for (String surahName : percentage.getSurahNames()) {
                String date = surahDates.getOrDefault(surahName, "");
                tableRows.add(new TableRow(surahName, date, percentage.getLang(), ""));
            }
        }
        tableAdapter.notifyDataSetChanged();
        Log.d(TAG, "Table rows updated successfully");
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
                // Start LoginActivity
                Intent intent = new Intent(SetoranMahasiswaActivity.this, LoginActivity.class);
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
