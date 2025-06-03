package com.example.localist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.localist.adapters.PicListAdapter;
import com.example.localist.databinding.ActivityDetailBinding;
import com.example.localist.models.ItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private ItemModel object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setupUI();
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

        // Load horizontal list
        ArrayList<String> picList = new ArrayList<>(object.getPic());
        binding.picList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        binding.picList.setAdapter(new PicListAdapter(picList, binding.mainPic));

        // Save to Firebase
        binding.favBtn.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && object != null) {
                DatabaseReference savedRef = FirebaseDatabase
                        .getInstance("https://localist-d63b7-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Saved").child(user.getUid());

                String id = object.getId() != null ? object.getId() : object.getTitle().replace(" ", "_");

                savedRef.child(id).setValue(object)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
