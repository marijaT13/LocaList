package com.example.localist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localist.databinding.ViewholderCategoryBinding;
import com.example.localist.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Viewholder>{
    private final List<CategoryModel> items;
    private Context context;

    public CategoryAdapter(List<CategoryModel> items){
        this.items=items;
    }

    @NonNull
    @Override
    public CategoryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        context = parent.getContext();
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(context),parent,false);
        return new Viewholder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.Viewholder holder, int position){
        CategoryModel item=items.get(position);
        holder.binding.title.setText(item.getName());

        Glide.with(context)
                .load(item.getImagePath())
                .into(holder.binding.pic);
    }
    @Override
    public int getItemCount(){
        return items.size();
    }
    public class Viewholder extends RecyclerView.ViewHolder{
        private final ViewholderCategoryBinding binding;
        public Viewholder(ViewholderCategoryBinding binding){
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
