package com.example.localist.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {

    private final ArrayList<ItemModel> items;
    private final Context context;

    public PopularAdapter(Context context, ArrayList<ItemModel> items) {
        this.context = context;
        this.items = items;
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
        ItemModel item = items.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.addressTxt.setText(item.getAddress());
        holder.binding.scoreTxt.setText(String.valueOf(item.getScore()));

        if (item.getPic() != null && !item.getPic().isEmpty()) {
            Glide.with(context)
                    .load(item.getPic().get(0))
                    .into(holder.binding.mainPic);
        }

        // Open detail screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });

        // Save bookmark
        holder.binding.favBtn.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference savedRef = FirebaseDatabase.getInstance().getReference("Saved").child(userId);
            String id = item.getId() != null ? item.getId() : item.getTitle().replace(" ", "_");

            savedRef.child(id).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                        holder.binding.favBtn.setImageResource(R.drawable.fav_icon);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

