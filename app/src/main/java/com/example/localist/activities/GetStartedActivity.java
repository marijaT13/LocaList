package com.example.localist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localist.R;
import com.example.localist.utils.LocaleHelper;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class GetStartedActivity extends AppCompatActivity {

    private Button btnRegister, btnContinueGuest, btnGoogle;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase)));
    }
    // Google Sign-In launcher
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

        mAuth = FirebaseAuth.getInstance();

        btnGoogle = findViewById(R.id.btn_gmail_register);
        btnRegister = findViewById(R.id.btn_register);
        btnContinueGuest = findViewById(R.id.btn_continue_guest);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // ensure it's in strings.xml
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Register
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(GetStartedActivity.this, RegisterActivity.class));
        });

        // Continue as Guest
        btnContinueGuest.setOnClickListener(v -> {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Signed in as Guest", Toast.LENGTH_SHORT).show();
                            goToMain();
                        } else {
                            Toast.makeText(this, "Guest sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Google Sign-In
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveGoogleUserToFirestoreIfNew(user);
                        }
                    } else {
                        Toast.makeText(this, "Firebase sign-in failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveGoogleUserToFirestoreIfNew(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(user.getUid());

        userRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                // Save user only if not already saved
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("uid", user.getUid());
                userMap.put("email", user.getEmail());
                userMap.put("name", user.getDisplayName());
                userMap.put("profile_pic", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
                userMap.put("created_at", FieldValue.serverTimestamp());

                userRef.set(userMap)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            goToMain();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Already exists
                goToMain();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error checking Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void goToMain() {
        startActivity(new Intent(GetStartedActivity.this, MainActivity.class));
        finish();
    }
}
