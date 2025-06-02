package com.example.localist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localist.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class GetStartedActivity extends AppCompatActivity {

    private Button btnRegister, btnContinueGuest, btnGoogle;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    // ✅ ActivityResultLauncher for Google Sign-In
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI Components
        btnGoogle = findViewById(R.id.btn_gmail_register);
        btnRegister = findViewById(R.id.btn_register);
        btnContinueGuest = findViewById(R.id.btn_continue_guest);

        // Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Register button
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(GetStartedActivity.this, RegisterActivity.class));
        });

        // Continue as Guest
        btnContinueGuest.setOnClickListener(v -> {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Signed in as Guest", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(GetStartedActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Guest sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Google Sign-In
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Signed in with Google", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GetStartedActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Firebase sign-in failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
