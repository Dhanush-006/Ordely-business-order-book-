package com.saveetha.orderly_book;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OrderRequest;
import com.saveetha.orderly_book.api.OrderResponse;
import com.saveetha.orderly_book.api.OwnerProductsResponse;
import com.saveetha.orderly_book.api.Product;
import com.saveetha.orderly_book.api.QuantityView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOwnerProductsActivity extends AppCompatActivity {

    private LinearLayout productsLayout;
    private TextView tvTotal, tvOwnerName;
    private Button btnPlaceOrder;

    private List<Product> productList = new ArrayList<>();
    private List<QuantityView> quantityViews = new ArrayList<>();

    private int ownerId, customerId;
    private double totalPrice = 0;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_products_customer);

        productsLayout = findViewById(R.id.productsLayout);
        tvTotal = findViewById(R.id.tvTotal);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        ownerId = getIntent().getIntExtra("owner_id", -1);
        customerId = getIntent().getIntExtra("customer_id", -1);
        String ownerName = getIntent().getStringExtra("owner_name");

        if (ownerId <= 0 || customerId <= 0) {
            Toast.makeText(this, "Invalid IDs", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvOwnerName.setText(ownerName + "'s Products");

        apiService = ApiClient.getClient().create(ApiService.class);
        fetchProducts();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void fetchProducts() {
        Call<OwnerProductsResponse> call = apiService.getOwnerProducts(ownerId);
        call.enqueue(new Callback<OwnerProductsResponse>() {
            @Override
            public void onResponse(Call<OwnerProductsResponse> call, Response<OwnerProductsResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equalsIgnoreCase(response.body().getStatus())) {
                    productList = response.body().getProducts();
                    displayProducts();
                } else {
                    Toast.makeText(CustomerOwnerProductsActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerProductsResponse> call, Throwable t) {
                Toast.makeText(CustomerOwnerProductsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProducts() {
        productsLayout.removeAllViews();
        quantityViews.clear();
        totalPrice = 0;

        for (Product p : productList) {
            QuantityView qv = new QuantityView(this, p); // custom view with name, price, quantity selector
            qv.setOnQuantityChangeListener(this::updateTotal);
            productsLayout.addView(qv.getView());
            quantityViews.add(qv);
        }

        updateTotal();
    }

    private void updateTotal() {
        totalPrice = 0;
        for (QuantityView qv : quantityViews) {
            int qty = qv.getQuantity();
            double price = 0;
            try {
                price = Double.parseDouble(qv.getProduct().getPrice());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            totalPrice += qty * price;
        }
        tvTotal.setText("Total: ₹" + totalPrice);
    }

    private void placeOrder() {
        List<OrderRequest.ProductOrder> productOrders = new ArrayList<>();

        for (QuantityView qv : quantityViews) {
            int qty = qv.getQuantity();
            if (qty > 0) {
                productOrders.add(new OrderRequest.ProductOrder(qv.getProduct().getId(), qty));
            }
        }

        if (productOrders.isEmpty()) {
            Toast.makeText(this, "Select at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        OrderRequest orderRequest = new OrderRequest(customerId, productOrders);

        apiService.placeOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse res = response.body();
                    if ("success".equals(res.getStatus())) {
                        Toast.makeText(CustomerOwnerProductsActivity.this,
                                "Order placed! Total: ₹" + res.getTotal_price(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(CustomerOwnerProductsActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerOwnerProductsActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(CustomerOwnerProductsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
