package com.example.utspemhir;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BerandaMahasiswaActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    String token;
    TextView namaUserTextView;
    TextView nimTextView;
    TextView semesterTextView;
    TextView namaTextView;
    TextView namaPaTextView;// TextView untuk nama dosen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berandamahasiswa);

        drawerLayout = findViewById(R.id.drawer_layer);

        // Mengakses TextViews dari sidebarmahasiswa.xml
        View sidebarView = findViewById(R.id.sidebarmahasiswa);// Pastikan ID ini sesuai dengan layout sidebarmahasiswa.xml
        //mengakses toolbar
        View toolbarView = findViewById(R.id.toolbar_mahasiswa);
        namaUserTextView = sidebarView.findViewById(R.id.namamahasiswa);
        nimTextView = sidebarView.findViewById(R.id.nim);
        semesterTextView = sidebarView.findViewById(R.id.semester);
        namaTextView = findViewById(R.id.mahasiswa_name);
        namaPaTextView = toolbarView.findViewById(R.id.nama_pa); // Inisialisasi TextView dosen_name

        // Ambil token dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("jwt_token", "");

        if (token.isEmpty()) {
            Log.e("TOKEN_ERROR", "Token not found in SharedPreferences");
            Toast.makeText(this, "Token tidak ditemukan. Silahkan login ulang.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Lakukan permintaan data menggunakan AsyncTask
        new FetchNIMDataTask().execute();
        new FetchProgressDataTask().execute();
    }
    public void detail(View view) {
        Intent intent = new Intent(BerandaMahasiswaActivity.this, ActivityProgress.class);
        startActivity(intent);
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void beranda(View view) {
        Intent intent = new Intent(BerandaMahasiswaActivity.this, BerandaMahasiswaActivity.class);
        startActivity(intent);
    }

    public void setoran(View view) {
        Intent intent = new Intent(BerandaMahasiswaActivity.this, SetoranMahasiswaActivity.class);
        startActivity(intent);
    }

    public void riwayat(View view) {
        Intent intent = new Intent(BerandaMahasiswaActivity.this, RiwayatMahasiswaActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        logoutMenu(BerandaMahasiswaActivity.this);
    }

    private void logoutMenu(BerandaMahasiswaActivity mainActivity) {
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

                Intent intent = new Intent(BerandaMahasiswaActivity.this, LoginActivity.class);
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

    private class FetchNIMDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String urlString = "https://samatif.xyz/login.php?action=get";
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

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
                    Log.e("FetchNIMDataTask", "Failed to fetch data. Response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("FetchNIMDataTask", "Response: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("nim")) {
                        String nim = jsonObject.getString("nim");

                        // Simpan NIM ke SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nim", nim);
                        editor.apply();

                        new FetchMahasiswaDataTask().execute(nim);
                        new FetchDosenNameTask().execute(nim); // Panggil AsyncTask untuk mengambil nama dosen
                    } else {
                        Log.e("FetchNIMDataTask", "NIM not found in response.");
                    }

                } catch (JSONException e) {
                    Log.e("FetchNIMDataTask", "JSON parsing error: " + e.getMessage());
                }
            } else {
                Log.e("FetchNIMDataTask", "Failed to fetch data. Result is null.");
            }
        }
    }
    private class FetchProgressDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String nim = sharedPreferences.getString("nim", "");
            String urlString = "https://samatif.xyz/setoran/sudahbelum.php?nim=" + nim;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

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
                    Log.e("FetchProgressDataTask", "Failed to fetch data. Response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("FetchProgressDataTask", "Response pre: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("percentages")) {
                        JSONArray percentagesArray = jsonObject.getJSONArray("percentages");

                        for (int i = 0; i < percentagesArray.length(); i++) {
                            JSONObject percentageObject = percentagesArray.getJSONObject(i);
                            String lang = percentageObject.getString("lang");
                            int percent = percentageObject.getInt("percent");

                            // Update progress bars with fetched data
                            if (lang.equals("Kerja Praktek")) {
                                ProgressBar progressBarKP = findViewById(R.id.barkp);
                                progressBarKP.setProgress(percent);
                                TextView textViewKP = findViewById(R.id.precentagekp);
                                textViewKP.setText(percent + "%");
                            } else if (lang.equals("Seminar Kerja Praktek")) {
                                ProgressBar progressBarSeminarKP = findViewById(R.id.barskp);
                                progressBarSeminarKP.setProgress(percent);
                                TextView textViewSeminarKP = findViewById(R.id.precentageskp);
                                textViewSeminarKP.setText(percent + "%");
                            }
                        }

                    } else {
                        Log.e("FetchProgressDataTask", "Percentage data not found in response.");
                    }

                } catch (JSONException e) {
                    Log.e("FetchProgressDataTask", "JSON parsing error: " + e.getMessage());
                }
            } else {
                Log.e("FetchProgressDataTask", "Failed to fetch data. Result is null.");
            }
        }

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
                connection.setRequestProperty("Authorization", "Bearer " + token);

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
                    // Parse the result as a JSON array
                    JSONArray jsonArray = new JSONArray(result);

                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        if (jsonObject.has("Nama") && jsonObject.has("NIM") && jsonObject.has("Semester")) {
                            String nama = jsonObject.getString("Nama");
                            String nim = jsonObject.getString("NIM");
                            int semester = jsonObject.getInt("Semester");

                            // Update TextViews with fetched data
                            namaUserTextView.setText(nama);
                            nimTextView.setText(nim);
                            semesterTextView.setText("Semester " + semester);
                            namaTextView.setText(nama);  // Update the mahasiswa_name TextView

                            // Simpan Nama ke SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("nama", nama);
                            editor.apply();

                            Log.d("FetchMahasiswaDataTask", "Nama: " + nama);
                            Log.d("FetchMahasiswaDataTask", "NIM: " + nim);
                            Log.d("FetchMahasiswaDataTask", "Semester: " + semester);
                        } else {
                            Log.e("FetchMahasiswaDataTask", "Required fields are missing in the response.");
                        }
                    } else {
                        Log.e("FetchMahasiswaDataTask", "Empty array in response.");
                    }

                } catch (JSONException e) {
                    Log.e("FetchMahasiswaDataTask", "JSON parsing error: " + e.getMessage());
                }
            } else {
                Log.e("FetchMahasiswaDataTask", "Failed to fetch data. Result is null.");
            }
        }
    }

    private class FetchDosenNameTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String nim = params[0];
            String urlString = "https://samatif.xyz/dosenpa/by-nim.php?nim=" + nim;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

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
                    Log.e("FetchDosenNameTask", "Failed to fetch data. Response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("FetchDosenNameTask", "Response: " + result);
                try {
                    // Parse the result as a JSON object
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray mahasiswaArray = jsonObject.getJSONArray("mahasiswa");

                        if (mahasiswaArray.length() > 0) {
                            JSONObject mahasiswaObject = mahasiswaArray.getJSONObject(0);

                            if (mahasiswaObject.has("Nama Dosen PA")) {
                                String namaDosen = mahasiswaObject.getString("Nama Dosen PA");

                                // Update TextView with fetched data
                                namaPaTextView.setText("PA."+namaDosen);

                                Log.d("FetchDosenNameTask", "Nama Dosen PA: " + namaDosen);
                            } else {
                                Log.e("FetchDosenNameTask", "Nama Dosen PA not found in response.");
                            }
                        } else {
                            Log.e("FetchDosenNameTask", "Empty mahasiswa array in response.");
                        }
                    } else {
                        Log.e("FetchDosenNameTask", "Response status is not success.");
                    }

                } catch (JSONException e) {
                    Log.e("FetchDosenNameTask", "JSON parsing error: " + e.getMessage());
                }
            } else {
                Log.e("FetchDosenNameTask", "Failed to fetch data. Result is null.");
            }
        }
    }
}
