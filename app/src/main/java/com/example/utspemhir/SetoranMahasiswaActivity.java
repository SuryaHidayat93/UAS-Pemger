package com.example.utspemhir;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

        // Get NIM and Token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nim = sharedPreferences.getString("nim", "");
        String token = sharedPreferences.getString("jwt_token", ""); // Get token from SharedPreferences

        if (nim.isEmpty() || token.isEmpty()) {
            Log.w(TAG, "NIM or token is empty or not available in SharedPreferences");
        } else {
            Log.d(TAG, "NIM from SharedPreferences: " + nim);
            fetchSurahData(nim, token);
            fetchSetoranData(nim, token);
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

    private void fetchSurahData(String nim, String token) {
        ApiService apiService = RetrofitClient.getClient("https://samatif.xyz/", token).create(ApiService.class);
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

    private void fetchSetoranData(String nim, String token) {
        ApiService apiService = RetrofitClient.getClient("https://samatif.xyz/", token).create(ApiService.class);
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
            String urlString = "https://samatif.xyz/mahasiswa/by-nim.php?nim=" + nim;
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
                try {
                    JSONArray jsonArray = new JSONArray(result); // Parse as JSONArray

                    // Assuming you only need the first object if multiple are returned
                    JSONObject jsonObject = jsonArray.getJSONObject(0); // Get the first object

                    String nama = jsonObject.getString("Nama"); // "Nama" is the key based on your example response
                    String nim = jsonObject.getString("NIM");
                    String semester = jsonObject.getString("Semester");

                    // Set TextViews with retrieved data
                    namaUserTextView.setText(nama);
                    nimTextView.setText(nim);
                    semesterTextView.setText("semester: "+semester);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("FetchMahasiswaDataTask", "Failed to fetch data.");
            }
        }
    }
    public void saveBitmap(View view) {
        Bitmap bitmap = getRecyclerViewScreenshot(recyclerView);
        if (bitmap != null) {
            saveBitmap(bitmap, "screenshot.png");
        } else {
            Log.e(TAG, "Failed to capture screenshot from RecyclerView");
        }
    }

    private Bitmap getRecyclerViewScreenshot(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        if (adapter == null) {
            return null;
        }

        Paint paint = new Paint();
        int height = 0;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> bitmapCache = new LruCache<>(cacheSize);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            TableAdapter.TableViewHolder holder = (TableAdapter.TableViewHolder) adapter.createViewHolder(view, adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);
            holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            Bitmap drawingCache = holder.itemView.getDrawingCache();
            if (drawingCache != null) {
                bitmapCache.put(String.valueOf(i), drawingCache);
            }
            height += holder.itemView.getMeasuredHeight();
        }

        Bitmap bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        bigCanvas.drawColor(Color.WHITE);
        height = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            Bitmap bitmap = bitmapCache.get(String.valueOf(i));
            if (bitmap != null) {
                bigCanvas.drawBitmap(bitmap, 0f, height, paint);
                height += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }

    private void saveBitmap(Bitmap bitmap, String fileName) {
        OutputStream outputStream;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                outputStream = getContentResolver().openOutputStream(uri);
            } else {
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, fileName);
                outputStream = new FileOutputStream(file);
            }

            bitmap.compress(CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(this, "Download successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error saving screenshot", e);
            Toast.makeText(this, "Failed to save screenshot", Toast.LENGTH_SHORT).show();
        }
    }
    public void downloadScreenshot(View view) {
        saveBitmap(view);  // Panggil metode saveBitmap yang sudah ada
    }
}
