package com.example.localist.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.localist.adapters.PicListAdapter;
import com.example.localist.databinding.ActivityDetailBinding;
import com.example.localist.models.ItemModel;
import com.example.localist.utils.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import com.example.localist.database.AppDatabase;
import com.example.localist.database.ItemDao;



public class DetailActivity extends AppCompatActivity {
    private ItemDao itemDao;
    private ActivityDetailBinding binding;
    private ItemModel object;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase)));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        itemDao = AppDatabase.getInstance(this).itemDao();

        getIntentExtra();
        setupUI();
        setupListeners();
    }

    private void getIntentExtra() {
        object = (ItemModel) getIntent().getSerializableExtra("object");
    }

    private void setupUI() {
        if (object == null) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set texts
        binding.titleTxt.setText(object.getTitle());
        binding.addressTxt.setText(object.getAddress());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getScore() + " Rating");
        binding.ratingBar.setRating((float) object.getScore());

        // Load main image
        if (object.getPic() != null && !object.getPic().isEmpty()) {
            Glide.with(this)
                    .load(object.getPic().get(0))
                    .into(binding.mainPic);
        }

        // Setup horizontal image list
        ArrayList<String> picList = new ArrayList<>(object.getPic());
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.picList.setAdapter(new PicListAdapter(picList, binding.mainPic));

        // Set content description for accessibility
        binding.backBtn.setContentDescription("Go back");
        binding.favBtn.setContentDescription("Save to favorites");
    }

    private void setupListeners() {
        // Back button - close activity
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // Save to Firebase favorite
        binding.favBtn.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && object != null) {
                // Firebase
                DatabaseReference savedRef = FirebaseDatabase
                        .getInstance("https://localist-d63b7-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Saved").child(user.getUid());

                String id = object.getId() != null ? object.getId() : object.getTitle().replace(" ", "_");
                object.setId(id); // Ensure ID is set

                savedRef.child(id).setValue(object)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Saved to Firebase", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save to Firebase", Toast.LENGTH_SHORT).show());

                // Room (in background thread)
                new Thread(() -> itemDao.insertItem(object)).start();
            } else {
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            }
        });


        // Read More button opens wikiUrl in browser
        binding.readMoreBtn.setOnClickListener(v -> {
            if (object.getWikiUrl() != null && !object.getWikiUrl().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getWikiUrl()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Wikipedia link not available", Toast.LENGTH_SHORT).show();
            }
        });
        // Book a weekend button opens bookingUrl in browser
        binding.bookingBtn.setOnClickListener(v -> {
            if (object.getBookingUrl() != null && !object.getBookingUrl().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getBookingUrl()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Booking link not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

}