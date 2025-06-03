package com.example.localist.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.localist.R;
import com.example.localist.databinding.ActivityProfileBinding;
import com.example.localist.fragments.IntroFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Show user data
        if (currentUser != null) {
            String name = currentUser.isAnonymous() ? "Guest" :
                    currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User";
            String email = currentUser.isAnonymous() ? "Anonymous User" : currentUser.getEmail();

            binding.profileName.setText(name);
            binding.profileEmail.setText(email);

            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this).load(currentUser.getPhotoUrl()).into(binding.profileImage);
            } else {
                binding.profileImage.setImageResource(R.drawable.default_avatar);
            }
        }

        // Bottom nav
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.bookmark) {
                 startActivity(new Intent(ProfileActivity.this, SavedActivity.class));
                return true;
            } else if (id == R.id.profile) {
                // Already here
                return true;
            }

            return false;
        });
        // Set the profile item as selected
        binding.bottomNavigationView.setSelectedItemId(R.id.profile);

        // Logout button
        binding.logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, IntroFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        // Delete Account Button
        binding.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        reAuthenticateAndDelete();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
    private void reAuthenticateAndDelete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        if (user.isAnonymous()) {
            deleteUserFirestoreData(user.getUid(), () -> {
                user.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                        goToIntro();
                    } else {
                        Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Re-authenticate");

            final EditText passwordInput = new EditText(this);
            passwordInput.setHint("Enter your password");
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(passwordInput);

            builder.setPositiveButton("Confirm", (dialog, which) -> {
                String password = passwordInput.getText().toString();
                String email = user.getEmail();

                if (email != null && !password.isEmpty()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential)
                            .addOnSuccessListener(unused -> {
                                deleteUserFirestoreData(user.getUid(), () -> {
                                    user.delete().addOnSuccessListener(unused1 -> {
                                        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                                        goToIntro();
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                    });
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Re-authentication failed. Wrong password?", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }
    private void deleteUserFirestoreData(String uid, Runnable onSuccess) {
        FirebaseFirestore.getInstance()
                .collection("users") // Replace with your actual collection name
                .document(uid)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "User data deleted", Toast.LENGTH_SHORT).show();
                    onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete Firestore data", Toast.LENGTH_SHORT).show();
                });
    }
    private void goToIntro() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, IntroFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Glide.get(this).clearMemory();
        finish();
    }

}
