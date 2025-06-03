package com.example.localist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.localist.R;
import com.example.localist.adapters.PopularAdapter;
import com.example.localist.databinding.ActivitySavedBinding;
import com.example.localist.models.ItemModel;
import com.example.localist.utils.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedActivity extends AppCompatActivity {

    private ActivitySavedBinding binding;
    private ArrayList<ItemModel> savedList;
    private PopularAdapter adapter;

    private DatabaseReference savedRef;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        savedList = new ArrayList<>();
        adapter = new PopularAdapter(this, savedList);
        binding.recyclerViewSaved.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerViewSaved.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            savedRef = FirebaseDatabase.getInstance("https://localist-d63b7-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Saved")
                    .child(user.getUid());
        }

        loadSavedItemsFromFirebase();
        setupBottomNavigation();
        setupClearButton();
    }

    private void loadSavedItemsFromFirebase() {
        if (savedRef == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        savedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                savedList.clear();
                for (com.google.firebase.database.DataSnapshot data : snapshot.getChildren()) {
                    ItemModel item = data.getValue(ItemModel.class);
                    if (item != null) {
                        savedList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(SavedActivity.this, "Failed to load saved items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClearButton() {
        binding.btnClearSaved.setOnClickListener(v -> {
            if (savedRef != null) {
                // Remove all saved items for this user
                savedRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SavedActivity.this, "Saved items cleared", Toast.LENGTH_SHORT).show();
                            savedList.clear();
                            adapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> Toast.makeText(SavedActivity.this, "Failed to clear saved items", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView.setSelectedItemId(R.id.bookmark);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                startActivity(new Intent(SavedActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.bookmark) {
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(SavedActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }
}
