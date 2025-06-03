package com.example.localist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.localist.R;
import com.example.localist.databinding.ActivityProfileBinding;
import com.example.localist.fragments.IntroFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    }
}
