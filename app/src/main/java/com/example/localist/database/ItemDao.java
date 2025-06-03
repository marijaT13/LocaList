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

    @Delete
    void deleteItem(ItemModel item);

    @Query("DELETE FROM saved_items")
    void clearAll();


    @Query("SELECT * FROM saved_items")
    List<ItemModel> getAllSavedItems();

}