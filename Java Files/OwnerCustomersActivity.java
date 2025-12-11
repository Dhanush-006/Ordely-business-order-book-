package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Customer;
import com.saveetha.orderly_book.api.CustomersAdapter;
import com.saveetha.orderly_book.api.OwnerCustomersResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerCustomersActivity extends AppCompatActivity {

    private RecyclerView rvCustomers;
    private CustomersAdapter adapter;
    private ApiService apiService;

    private int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_customers);

        rvCustomers = findViewById(R.id.rvCustomers);
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient().create(ApiService.class);

        ownerId = getIntent().getIntExtra("owner_id", -1);
        if (ownerId <= 0) {
            Toast.makeText(this, "Invalid owner ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchCustomers();
    }

    private void fetchCustomers() {
        Call<OwnerCustomersResponse> call = apiService.getOwnerCustomers(ownerId);
        call.enqueue(new Callback<OwnerCustomersResponse>() {
            @Override
            public void onResponse(Call<OwnerCustomersResponse> call, Response<OwnerCustomersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customers = response.body().getCustomers();
                    if (customers != null && !customers.isEmpty()) {
                        adapter = new CustomersAdapter(customers, customer -> {
                            // On customer click → open bar chart activity
                            Intent intent = new Intent(OwnerCustomersActivity.this, CustomerOrdersActivity.class);
                            intent.putExtra("customer_id", customer.getId());
                            intent.putExtra("owner_id", ownerId);
                            startActivity(intent);
                        });
                        rvCustomers.setAdapter(adapter);
                    } else {
                        Toast.makeText(OwnerCustomersActivity.this, "No customers found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OwnerCustomersActivity.this, "Failed to fetch customers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerCustomersResponse> call, Throwable t) {
                Toast.makeText(OwnerCustomersActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
