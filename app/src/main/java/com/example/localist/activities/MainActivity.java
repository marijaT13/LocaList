package com.example.localist.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.localist.R;
import com.example.localist.adapters.PopularAdapter;
import com.example.localist.databinding.ActivityMainBinding;
import com.example.localist.fragments.AboutDialogFragment;
import com.example.localist.models.ItemModel;
import com.example.localist.utils.LocaleHelper;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final String DATABASE_URL = "https://localist-d63b7-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    private ArrayList<ItemModel> popularList = new ArrayList<>();
    private PopularAdapter popularAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Request permission for notifications
        requestNotificationPermission();

        // Fetch FCM Token
        fetchFCMToken();

        // Show startup notification
        showStartupNotification();

        // Load popular items
        loadPopularItems();

        // Search functionality
        EditText searchInput = findViewById(R.id.search_edit_text);
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                popularAdapter.filterItems(query);  // Filter items based on the query
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        // Setup bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                return true;
            } else if (id == R.id.bookmark) {
                startActivity(new Intent(MainActivity.this, SavedActivity.class));
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });

        // Show AboutDialogFragment when info button is clicked
        ImageView infoBtn = findViewById(R.id.info_btn);
        if (infoBtn != null) {
            infoBtn.setOnClickListener(v -> {
                AboutDialogFragment dialog = new AboutDialogFragment();
                dialog.show(getSupportFragmentManager(), "about_dialog");
            });
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    private void fetchFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);
                    // You can send this token to your server if needed
                    saveTokenToFirestore(token);
                });
    }

    private void loadPopularItems() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        DatabaseReference popularRef = database.getReference("Popular");

        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                popularList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    ItemModel item = child.getValue(ItemModel.class);
                    if (item != null) popularList.add(item);
                }

                // Set up the RecyclerView with the adapter and the layout manager
                popularAdapter = new PopularAdapter(MainActivity.this, popularList);
                GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
                binding.recyclerViewPopular.setLayoutManager(layoutManager);
                binding.recyclerViewPopular.setAdapter(popularAdapter);

                binding.progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveTokenToFirestore(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // You can use FirebaseAuth.getUid() if users are signed in
        String userId = "user_" + System.currentTimeMillis();

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token", token);
        tokenData.put("timestamp", System.currentTimeMillis());

        db.collection("user_tokens").document(userId)
                .set(tokenData)
                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token saved to Firestore successfully"))
                .addOnFailureListener(e -> Log.e("FCM", "Error saving token to Firestore", e));
    }

    private void showStartupNotification() {
        String channelId = "localist_channel";
        String channelName = "Localist Notifications";

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.bell_icon)
                .setContentTitle("Welcome to Localist!")
                .setContentText("Discover the best places across Macedonia.")
                .setAutoCancel(true);

        manager.notify(1001, builder.build());
    }
}
