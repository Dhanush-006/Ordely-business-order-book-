package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OwnersAdapter;
import com.saveetha.orderly_book.api.OwnersResponse;
import com.saveetha.orderly_book.api.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrderActivity extends AppCompatActivity {

    private RecyclerView rvOwners;
    private List<User> ownersList = new ArrayList<>();
    private ApiService apiService;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        rvOwners = findViewById(R.id.rvOwners);
        rvOwners.setLayoutManager(new LinearLayoutManager(this));

        customerId = getIntent().getIntExtra("customer_id", -1);
        if (customerId <= 0) {
            Toast.makeText(this, "Invalid Customer ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);
        fetchOwners();
    }

    private void fetchOwners() {
        Call<OwnersResponse> call = apiService.getOwners();
        call.enqueue(new Callback<OwnersResponse>() {
            @Override
            public void onResponse(Call<OwnersResponse> call, Response<OwnersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ownersList.clear();
                    ownersList.addAll(response.body().getOwners());

                    OwnersAdapter adapter = new OwnersAdapter(ownersList, owner -> {
                        // Open CustomerOwnerProductsActivity
                        Intent intent = new Intent(PlaceOrderActivity.this, CustomerOwnerProductsActivity.class);
                        intent.putExtra("owner_id", owner.getId());
                        intent.putExtra("customer_id", customerId);
                        intent.putExtra("owner_name", owner.getName());
                        startActivity(intent);
                    });
                    rvOwners.setAdapter(adapter);

                } else {
                    Toast.makeText(PlaceOrderActivity.this, "No owners found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnersResponse> call, Throwable t) {
                Toast.makeText(PlaceOrderActivity.this, "Failed to fetch owners", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
