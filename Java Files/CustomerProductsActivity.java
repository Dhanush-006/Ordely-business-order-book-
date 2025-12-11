package com.saveetha.orderly_book;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Product;
import com.saveetha.orderly_book.api.ProductAdapter;
import com.saveetha.orderly_book.api.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProductsActivity extends AppCompatActivity {

    private RecyclerView rvCustomerProducts;
    private ProductAdapter adapter;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_products);

        rvCustomerProducts = findViewById(R.id.rvCustomerProducts);
        rvCustomerProducts.setLayoutManager(new LinearLayoutManager(this));

        customerId = getIntent().getIntExtra("customer_id", -1);

        // Customer view → can order (isCustomer = true)
        adapter = new ProductAdapter(new ArrayList<>(), product -> placeOrder(product.getId()), true);
        rvCustomerProducts.setAdapter(adapter);

        loadAllProducts();
    }

    private void loadAllProducts() {
        int ownerId = getIntent().getIntExtra("owner_id", -1);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ProductResponse> call = apiService.getAllProducts(ownerId);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getProducts();
                    adapter.updateProducts(products);
                } else {
                    Toast.makeText(CustomerProductsActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(CustomerProductsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void placeOrder(int productId) {
        // Implement your place order logic here
        Toast.makeText(this, "Order placed for product ID: " + productId, Toast.LENGTH_SHORT).show();
    }
}
