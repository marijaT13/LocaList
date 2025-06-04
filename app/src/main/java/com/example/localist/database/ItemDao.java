package com.example.localist.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.localist.models.ItemModel;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert
    void insertItem(ItemModel item);

    @Insert
    void insertItems(List<ItemModel> items);  // New method for inserting a list of items

    @Query("SELECT * FROM saved_items")  // Example query
    List<ItemModel> getAllSavedItems();


    @Query("DELETE FROM saved_items")  // Example query for clearing the table
    void clearAll();
}
