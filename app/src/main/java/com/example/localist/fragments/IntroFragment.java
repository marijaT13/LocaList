package com.example.localist.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.localist.R;
import com.example.localist.activities.LoginActivity;
import com.example.localist.activities.RegisterActivity;

public class IntroFragment extends AppCompatActivity {

    private AppCompatButton register;
    private AppCompatButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_intro); // Make sure this XML has your layout

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        register.setOnClickListener(v -> {
            startActivity(new Intent(IntroFragment.this, RegisterActivity.class));
        });

        login.setOnClickListener(v -> {
            startActivity(new Intent(IntroFragment.this, LoginActivity.class));
        });
    }
}
