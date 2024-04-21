package com.example.utspemhir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username;
    EditText password;
    Button loginButton;

    // Data dummy untuk login
    private static final String DUMMY_MAHASISWA_USERNAME = "mahasiswa";
    private static final String DUMMY_MAHASISWA_PASSWORD = "123";
    private static final String DUMMY_DOSEN_USERNAME = "dosen";
    private static final String DUMMY_DOSEN_PASSWORD = "456";

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
        if (v.getId() == R.id.loginButton){
            String inputUsername = username.getText().toString();
            String inputPassword = password.getText().toString();

            if (inputUsername.equals(DUMMY_MAHASISWA_USERNAME) && inputPassword.equals(DUMMY_MAHASISWA_PASSWORD)) {
                Intent intent = new Intent(LoginActivity.this, BerandaMahasiswaActivity.class);
                startActivity(intent);
            } else if (inputUsername.equals(DUMMY_DOSEN_USERNAME) && inputPassword.equals(DUMMY_DOSEN_PASSWORD)) {
                Intent intent = new Intent(LoginActivity.this, BerandaDosenActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void forgotPasswordClicked(View view) {
        // Intent untuk membuka halaman Lupa Password
        Intent intent = new Intent(LoginActivity.this, LupaPasswordActivity.class);
        startActivity(intent);
    }

}
