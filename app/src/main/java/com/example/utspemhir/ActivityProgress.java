package com.example.utspemhir;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityProgress extends AppCompatActivity {

    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5;
    private TextView percentage1, percentage2, percentage3, percentage4, percentage5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nim = sharedPreferences.getString("nim", "");
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);
        progressBar5 = findViewById(R.id.progressBar5);

        percentage1 = findViewById(R.id.percentage1);
        percentage2 = findViewById(R.id.percentage2);
        percentage3 = findViewById(R.id.percentage3);
        percentage4 = findViewById(R.id.percentage4);
        percentage5 = findViewById(R.id.percentage5);

        // Dapatkan NIM dari Intent


        // Jalankan AsyncTask untuk mengambil data dari API
        new FetchProgressData().execute(nim);
    }

    private class FetchProgressData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String nim = params[0];
            String urlString = "https://samatif-ml.preview-domain.com/setoran/sudahbelum.php?nim=" + nim;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    return response.toString();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray percentagesArray = jsonObject.getJSONArray("percentages");

                    for (int i = 0; i < percentagesArray.length(); i++) {
                        JSONObject percentageObject = percentagesArray.getJSONObject(i);
                        String lang = percentageObject.getString("lang");
                        int percent = percentageObject.getInt("percent");

                        switch (lang) {
                            case "Kerja Praktek":
                                setProgressWithAnimation(progressBar1, percentage1, percent);
                                break;
                            case "Seminar Kerja Praktek":
                                setProgressWithAnimation(progressBar2, percentage2, percent);
                                break;
                            case "Judul Tugas Akhir":
                                setProgressWithAnimation(progressBar3, percentage3, percent);
                                break;
                            case "Seminar Proposal":
                                setProgressWithAnimation(progressBar4, percentage4, percent);
                                break;
                            case "Sidang Tugas Akhir":
                                setProgressWithAnimation(progressBar5, percentage5, percent);
                                break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void setProgressWithAnimation(ProgressBar progressBar, TextView percentageView, int percent) {
            ObjectAnimator.ofInt(progressBar, "progress", percent)
                    .setDuration(1000)  // 1 second
                    .start();

            percentageView.setText(percent + "%");
        }
    }
}
