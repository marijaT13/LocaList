package com.example.localist.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.localist.adapters.PicListAdapter;
import com.example.localist.databinding.ActivityDetailBinding;
import com.example.localist.models.ItemModel;

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
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.picList.setAdapter(new PicListAdapter(picList, binding.mainPic));


    }
}
