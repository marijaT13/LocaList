package com.example.localist.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.localist.database.AppDatabase;
import com.example.localist.database.ItemDao;
import com.example.localist.models.ItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ItemRepository {

    private final ItemDao itemDao;
    private final DatabaseReference firebaseRef;

    public ItemRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "localist_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // For simplicity — use async in production
                .build();

        this.itemDao = db.itemDao();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            firebaseRef = FirebaseDatabase.getInstance("https://localist-d63b7-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Saved")
                    .child(user.getUid());
        } else {
            firebaseRef = null;
        }
    }

    // Save to Room
    public void saveToRoom(ItemModel item) {
        itemDao.insertItem(item);
    }

    // Save to Firebase
    public void saveToFirebase(ItemModel item) {
        if (firebaseRef != null && item.getId() != null) {
            firebaseRef.child(item.getId()).setValue(item);
        }
    }

    // Save to both
    public void saveItem(ItemModel item) {
        saveToRoom(item);
        saveToFirebase(item);
    }

    // Get all saved items from Room
    public List<ItemModel> getSavedItemsFromRoom() {
        return itemDao.getAllSavedItems();
    }

    // Clear all saved items from Room
    public void clearSavedItemsFromRoom() {
        itemDao.clearAll();
    }
}
