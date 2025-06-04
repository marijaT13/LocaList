package com.example.localist.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localist.R;
import com.example.localist.activities.DetailActivity;
import com.example.localist.databinding.ViewholderPopularBinding;
import com.example.localist.models.ItemModel;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {

    private final List<ItemModel> originalItems;  // List to keep the original items
    private List<ItemModel> filteredItems;       // List to keep filtered items based on search
    private final Context context;
    private FirebaseAnalytics firebaseAnalytics;

    public PopularAdapter(Context context, List<ItemModel> items) {
        this.context = context;
        this.originalItems = new ArrayList<>(items);  // Store the original items
        this.filteredItems = new ArrayList<>(items);  // Initially, show all items
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ItemModel item = filteredItems.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.addressTxt.setText(item.getAddress());
        holder.binding.scoreTxt.setText(String.valueOf(item.getScore()));

        if (item.getPic() != null && !item.getPic().isEmpty()) {
            Glide.with(context)
                    .load(item.getPic().get(0))
                    .into(holder.binding.mainPic);
        }

        // Open Detail Activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);

            // Log Firebase Analytics event when an item is clicked
            Bundle bundle = new Bundle();
            bundle.putString("item_title", item.getTitle());
            firebaseAnalytics.logEvent("item_clicked", bundle);
        });

        // Save to bookmarks
        holder.binding.favBtn.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference savedRef = FirebaseDatabase.getInstance().getReference("Saved").child(userId);
            String id = item.getId() != null ? item.getId() : item.getTitle().replace(" ", "_");

            savedRef.child(id).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                        holder.binding.favBtn.setImageResource(R.drawable.fav_icon);

                        // Log Firebase Analytics event when an item is saved
                        Bundle bundle = new Bundle();
                        bundle.putString("item_title", item.getTitle());
                        firebaseAnalytics.logEvent("item_saved", bundle);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    // Method to filter items based on search query
    public void filterItems(String query) {
        if (query == null || query.isEmpty()) {
            filteredItems = new ArrayList<>(originalItems);  // Show all items if search query is empty
        } else {
            List<ItemModel> filteredList = new ArrayList<>();
            for (ItemModel item : originalItems) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        item.getAddress().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);  // Add items matching the query
                }
            }
            filteredItems = filteredList;  // Update the filtered list
        }
        notifyDataSetChanged();  // Notify adapter about the data change
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
