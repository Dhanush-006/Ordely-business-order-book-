package com.saveetha.orderly_book;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OrderRequest;
import com.saveetha.orderly_book.api.OrderResponse;
import com.saveetha.orderly_book.api.Product;
import com.saveetha.orderly_book.api.ProductsAdapter;
import com.saveetha.orderly_book.api.OwnerProductsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProductsCustomerActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private TextView tvOwnerName, tvTotal;
    private Button btnPlaceOrder;

    private ProductsAdapter productsAdapter;
    private ApiService apiService;

    private int ownerId, customerId;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_products_customer);

        // Initialize views
        rvProducts = findViewById(R.id.rvOwnerProducts);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        ownerId = getIntent().getIntExtra("owner_id", -1);
        customerId = getIntent().getIntExtra("customer_id", -1);

        if (ownerId <= 0 || customerId <= 0) {
            Toast.makeText(this, "Invalid IDs", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        String ownerName = getIntent().getStringExtra("owner_name");
        tvOwnerName.setText(ownerName != null ? ownerName + "'s Products" : "Owner's Products");

        fetchProducts();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void fetchProducts() {
        Call<OwnerProductsResponse> call = apiService.getOwnerProducts(ownerId);
        call.enqueue(new Callback<OwnerProductsResponse>() {
            @Override
            public void onResponse(Call<OwnerProductsResponse> call, Response<OwnerProductsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getProducts();
                    if (products != null && !products.isEmpty()) {
                        setupProductsRecycler(products);
                    } else {
                        Toast.makeText(OwnerProductsCustomerActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OwnerProductsCustomerActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerProductsResponse> call, Throwable t) {
                Toast.makeText(OwnerProductsCustomerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupProductsRecycler(List<Product> products) {
        productsAdapter = new ProductsAdapter(products);

        // Listen to quantity changes to update total
        productsAdapter.setOnQuantityChangeListener(() -> {
            totalPrice = productsAdapter.getTotal();
            tvTotal.setText("Total: ₹" + totalPrice);
        });

        rvProducts.setAdapter(productsAdapter);
    }

    private void placeOrder() {
        if (productsAdapter == null) return;

        List<OrderRequest.ProductOrder> selectedProducts = productsAdapter.getSelectedProducts();
        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Select at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderRequest orderRequest = new OrderRequest(customerId, selectedProducts);

        apiService.placeOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        Toast.makeText(OwnerProductsCustomerActivity.this,
                                "Order placed! Total: ₹" + res.getTotal_price(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(OwnerProductsCustomerActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OwnerProductsCustomerActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(OwnerProductsCustomerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
