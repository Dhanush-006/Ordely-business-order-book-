package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Owner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProfileActivity extends AppCompatActivity {

    private TextView tvOwnerName, tvBusinessName, tvOwnerEmail, tvOwnerPhone;
    private CardView cardBusinessOrders, cardSettings, cardHelpSupport, cardLogout;
    private BottomNavigationView bottomNavigationOwner;
    private int userId; // now this is the user id
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);

        // Initialize UI
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvOwnerEmail = findViewById(R.id.tvOwnerEmail);
        tvOwnerPhone = findViewById(R.id.tvOwnerPhone);

        cardBusinessOrders = findViewById(R.id.cardBusinessOrders);
        cardSettings = findViewById(R.id.cardSettings);
        cardHelpSupport = findViewById(R.id.cardHelp);
        cardLogout = findViewById(R.id.cardLogout);

        bottomNavigationOwner = findViewById(R.id.bottomNavigationOwner);

        // Get user id from intent
        userId = getIntent().getIntExtra("user_id", -1); // changed from owner_id to user_id
        if (userId <= 0) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        // Fetch owner data from API
        fetchOwnerDetails(userId);

        // Set card click listeners
        cardBusinessOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessOrdersActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        cardHelpSupport.setOnClickListener(v -> startActivity(new Intent(this, HelpSupportActivity.class)));

        cardLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bottom navigation using if-else
        bottomNavigationOwner.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_owner_home) {
                startActivity(new Intent(this, OwnerHomeActivity.class).putExtra("user_id", userId));
                return true;
            } else if (id == R.id.nav_owner_products) {
                startActivity(new Intent(this, OwnerProductsActivity.class).putExtra("user_id", userId));
                return true;
            } else if (id == R.id.nav_owner_orders) {
                startActivity(new Intent(this, BusinessOrdersActivity.class).putExtra("user_id", userId));
                return true;
            } else if (id == R.id.nav_owner_profile) {
                return true;
            }
            return false;
        });
    }

    private void fetchOwnerDetails(int id) {
        // Use the API to fetch user by id and role=owner
        Call<Owner> call = apiService.getOwnerByUserId(id);
        call.enqueue(new Callback<Owner>() {
            @Override
            public void onResponse(Call<Owner> call, Response<Owner> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Owner owner = response.body();
                    tvOwnerName.setText(owner.getName() != null ? owner.getName() : "N/A");
                    tvBusinessName.setText(owner.getName() != null ? owner.getName() + "'s Business" : "My Business");
                    tvOwnerEmail.setText(owner.getEmail() != null ? owner.getEmail() : "N/A");
                    tvOwnerPhone.setText(owner.getContact() != null ? owner.getContact() : "N/A");
                } else {
                    Toast.makeText(OwnerProfileActivity.this, "Failed to load owner data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Owner> call, Throwable t) {
                Toast.makeText(OwnerProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
