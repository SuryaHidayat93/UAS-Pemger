package com.example.utspemhir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LupaPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupapassword);
    }
    public void backLoginClicked (View view) {
        // Intent untuk membuka halaman Lupa Password
        Intent intent = new Intent(LupaPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}