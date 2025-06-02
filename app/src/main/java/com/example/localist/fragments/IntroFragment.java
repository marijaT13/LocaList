package com.example.localist.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.localist.R;
import com.example.localist.activities.GetStartedActivity;
import com.example.localist.activities.LoginActivity;

public class IntroFragment extends AppCompatActivity {

    private AppCompatButton btnGetStarted;
    private AppCompatButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_intro);

        btnGetStarted = findViewById(R.id.btn_get_started);
        login = findViewById(R.id.login);

        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(IntroFragment.this, GetStartedActivity.class));
        });

        login.setOnClickListener(v -> {
            startActivity(new Intent(IntroFragment.this, LoginActivity.class));
        });
    }
}
