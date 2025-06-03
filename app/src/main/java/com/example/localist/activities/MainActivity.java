package com.example.localist.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.localist.adapters.PopularAdapter;
import com.example.localist.databinding.ActivityMainBinding;
import com.example.localist.models.ItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Firebase Realtime Database URL (adjusted for your region)
    private final String DATABASE_URL = "https://localist-d63b7-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load data into RecyclerView
        loadPopularItems();
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

                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        ItemModel item = child.getValue(ItemModel.class);
                        if (item != null) {
                            popularList.add(item);
                        }
                    }

                    // Use GridLayoutManager for 2-column vertical display
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
                    binding.recyclerViewPopular.setLayoutManager(gridLayoutManager);

                    PopularAdapter adapter = new PopularAdapter(MainActivity.this, popularList);
                    binding.recyclerViewPopular.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                }

                binding.progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }
}
