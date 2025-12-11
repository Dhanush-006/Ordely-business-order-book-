package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OwnerProductsAdapter;
import com.saveetha.orderly_book.api.OwnerProductsResponse;
import com.saveetha.orderly_book.api.Product;
import com.saveetha.orderly_book.api.ProductsAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProductsActivity extends AppCompatActivity {

    private TextView tvBusinessSubtitle;
    private RecyclerView rvProducts;
    private CardView cardMyCustomers;

    private ProductsAdapter productsAdapter;
    private ApiService apiService;

    private int ownerId;
    private String ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_products);

        tvBusinessSubtitle = findViewById(R.id.tvBusinessSubtitle);
        rvProducts = findViewById(R.id.rvOwnerProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        cardMyCustomers = findViewById(R.id.cardMyCustomers);

        apiService = ApiClient.getClient().create(ApiService.class);

        ownerId = getIntent().getIntExtra("owner_id", -1);
        if (ownerId <= 0) {
            ownerId = getIntent().getIntExtra("user_id", -1); // fallback
        }

        ownerName = "My Business"; // or fetch dynamically from API if needed

        if (ownerId <= 0) {
            Toast.makeText(this, "Invalid owner info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        tvBusinessSubtitle.setText(ownerName + "'s Products");

        fetchOwnerProducts();

        cardMyCustomers.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerProductsActivity.this, OwnerCustomersActivity.class);
            intent.putExtra("owner_id", ownerId);
            startActivity(intent);
        });
    }

    private void fetchOwnerProducts() {
        Call<OwnerProductsResponse> call = apiService.getOwnerProducts(ownerId);
        call.enqueue(new Callback<OwnerProductsResponse>() {
            @Override
            public void onResponse(Call<OwnerProductsResponse> call, Response<OwnerProductsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    List<Product> products = response.body().getProducts();
                    if (products != null && !products.isEmpty()) {
                        OwnerProductsAdapter adapter = new OwnerProductsAdapter(products);
                        rvProducts.setAdapter(adapter);


                    } else {
                        Toast.makeText(OwnerProductsActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(OwnerProductsActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerProductsResponse> call, Throwable t) {
                Toast.makeText(OwnerProductsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
