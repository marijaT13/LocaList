package com.example.localist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.localist.R;
import com.example.localist.adapters.PopularAdapter;
import com.example.localist.databinding.ActivityMainBinding;
import com.example.localist.models.ItemModel;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final String DATABASE_URL = "https://localist-d63b7-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadPopularItems();
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                // Already here
                return true;
            } else if (id == R.id.bookmark) {
                //startActivity(new Intent(MainActivity.this, BookmarkActivity.class));
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }

    private void loadPopularItems() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        DatabaseReference popularRef = database.getReference("Popular");

        ArrayList<ItemModel> popularList = new ArrayList<>();

        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                popularList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    ItemModel item = child.getValue(ItemModel.class);
                    if (item != null) popularList.add(item);
                }

                GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
                binding.recyclerViewPopular.setLayoutManager(layoutManager);
                binding.recyclerViewPopular.setAdapter(new PopularAdapter(MainActivity.this, popularList));

                binding.progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }
}
