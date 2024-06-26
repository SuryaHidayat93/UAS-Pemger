package com.example.utspemhir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username;
    EditText password;
    Button loginButton;
    TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotpassword);

        loginButton.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton) {
            String inputUsername = username.getText().toString();
            String inputPassword = password.getText().toString();

            new LoginTask().execute(inputUsername, inputPassword);
        } else if (v.getId() == R.id.forgotpassword) {
            Intent intent = new Intent(LoginActivity.this, LupaPasswordActivity.class);
            startActivity(intent);
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String urlString = "https://samatif.xyz/login.php?action=login";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                JSONObject postData = new JSONObject();
                postData.put("username", username);
                postData.put("password", password);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                connection.disconnect();

                String finalResult = result.toString();
                Log.d("LOGIN_RESPONSE", "Server response: " + finalResult);

                return finalResult;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("LOGIN_ERROR", "Error in doInBackground: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    Log.d("LOGIN_RESULT", "Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("token")) {
                        String token = jsonObject.getString("token");

                        saveToken(token);

                        Log.d("TOKEN_RECEIVED", "Token: " + token);

                        getData(token);
                    } else {
                        Toast.makeText(LoginActivity.this, "Password atau Username salah", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("LOGIN_ERROR", "Error in onPostExecute: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Gagal memproses data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("LOGIN_ERROR", "Result is null");
                Toast.makeText(LoginActivity.this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveToken(String token) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jwt_token", token);
            editor.apply();
        }

        private void getData(String token) {
            new GetDataTask().execute(token);
        }
    }

    private class GetDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            String urlString = "https://samatif.xyz/login.php?action=get";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                connection.disconnect();

                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("GET_DATA_ERROR", "Error in doInBackground: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    Log.d("GET_DATA_RESULT", "Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);

                    String role = jsonObject.getString("role");

                    Log.d("ROLE_INFO", "Role: " + role);

                    saveRole(role); // Simpan role ke SharedPreferences

                    Intent intent;
                    if (role.equals("mahasiswa")) {
                        intent = new Intent(LoginActivity.this, BerandaMahasiswaActivity.class);
                    } else if (role.equals("dosen")) {
                        intent = new Intent(LoginActivity.this, BerandaDosenActivity.class);
                        intent.putExtra("USERNAME", jsonObject.getString("username"));
                    } else {
                        Toast.makeText(LoginActivity.this, "Role tidak dikenali", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("GET_DATA_ERROR", "Error in onPostExecute: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Gagal memproses data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("GET_DATA_ERROR", "Result is null");
                Toast.makeText(LoginActivity.this, "Gagal mengambil data dari server", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveRole(String role) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_role", role);
            editor.apply();
        }
    }
}
