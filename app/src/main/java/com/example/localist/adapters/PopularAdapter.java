package com.example.localist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localist.databinding.ViewholderPopularBinding;
import com.example.localist.models.ItemModel;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {
    ArrayList<ItemModel> items;
    Context context;
    ViewholderPopularBinding binding;
    public PopularAdapter(ArrayList<ItemModel> items){
        this.items=items;
    }
    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        binding=ViewholderPopularBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        context=parent.getContext();
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, int position){
        binding.titleTxt.setText(items.get(position).getTitle());
        binding.addressTxt.setText(items.get(position).getAddress());
        binding.scoreTxt.setText("" + items.get(position).getScore());

        Glide.with(context)
                .load(items.get(position).getPic().get(0))
                .into(binding.mainPic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public int getItemCount(){
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
        }
    }
}
