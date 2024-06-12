package com.example.utspemhir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username;
    EditText password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton) {
            String inputUsername = username.getText().toString();
            String inputPassword = password.getText().toString();

            // Lakukan login
            new LoginTask().execute(inputUsername, inputPassword);
        }
    }

    public void forgotPasswordClicked(View view) {
        // Intent untuk membuka halaman Lupa Password
        Intent intent = new Intent(LoginActivity.this, LupaPasswordActivity.class);
        startActivity(intent);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String urlString = "https://samatif.000webhostapp.com/login/user.php?username=" + username + "&password=" + password;

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

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
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("message");

                    if (message.equals("Successful login.")) {
                        String role = jsonObject.getString("Role");
                        String username = jsonObject.getString("Username");
                        Intent intent;

                        if (role.equals("mahasiswa")) {
                            intent = new Intent(LoginActivity.this, BerandaMahasiswaActivity.class);
                        } else if (role.equals("dosen")) {
                            intent = new Intent(LoginActivity.this, BerandaDosenActivity.class);
                            // Pass username to BerandaDosenActivity
                            intent.putExtra("USERNAME", username);
                        } else {
                            Toast.makeText(LoginActivity.this, "Role tidak dikenali", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Username atau password salah", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Gagal memproses data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
