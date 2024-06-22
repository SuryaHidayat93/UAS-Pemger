package com.example.utspemhir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LupaPasswordActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordBaruEditText;
    private EditText konfirmasiPasswordEditText;
    private Button resetPasswordButton;
    private Button kembaliLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupapassword);

        usernameEditText = findViewById(R.id.username);
        passwordBaruEditText = findViewById(R.id.passwordbaru);
        konfirmasiPasswordEditText = findViewById(R.id.konfirmasispassword);
        resetPasswordButton = findViewById(R.id.resetpassword);
        kembaliLoginButton = findViewById(R.id.kembalilogin);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResetPassword();
            }
        });

        kembaliLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backLoginClicked(v);
            }
        });
    }

    private void handleResetPassword() {
        String username = usernameEditText.getText().toString().trim();
        String passwordBaru = passwordBaruEditText.getText().toString().trim();
        String konfirmasiPassword = konfirmasiPasswordEditText.getText().toString().trim();

        if (username.isEmpty() || passwordBaru.isEmpty() || konfirmasiPassword.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordBaru.equals(konfirmasiPassword)) {
            Toast.makeText(this, "Password tidak sesuai", Toast.LENGTH_SHORT).show();
            return;
        }

        new ResetPasswordTask().execute(username, passwordBaru);
    }

    public void backLoginClicked(View view) {
        Intent intent = new Intent(LupaPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private class ResetPasswordTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String passwordBaru = params[1];
            String urlString = "https://samatif.xyz/lupapassword.php"; // Change to your actual endpoint

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject postData = new JSONObject();
                postData.put("username", username);
                postData.put("password", passwordBaru);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Password berhasil direset!";
                } else {
                    return "Gagal mereset password.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Gagal terhubung ke server.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(LupaPasswordActivity.this, result, Toast.LENGTH_SHORT).show();

            if (result.equals("Password berhasil direset!")) {
                Intent intent = new Intent(LupaPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
