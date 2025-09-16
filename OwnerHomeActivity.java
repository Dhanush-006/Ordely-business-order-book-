package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Order;
import com.saveetha.orderly_book.api.OrderResponse;
import com.saveetha.orderly_book.api.OrdersAdapter;
import com.saveetha.orderly_book.api.OwnerOrdersResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerHomeActivity extends AppCompatActivity {

    private Button btnMyProducts, btnAddProduct, btnBusinessOrders;
    private RecyclerView rvOrders;
    private OrdersAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private int userId; // now using userId from users table
    private ApiService apiService;
    private BottomNavigationView bottomNavigationOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        // Initialize UI
        btnMyProducts = findViewById(R.id.btnOwnerProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnBusinessOrders = findViewById(R.id.btnBusinessOrders);
        rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigationOwner = findViewById(R.id.bottomNavigationOwner);

        // Set BottomNavigation listener
        bottomNavigationOwner.setOnItemSelectedListener(this::handleBottomNav);

        // Get user_id from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        // Button listeners
        btnMyProducts.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerProductsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProductActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        btnBusinessOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessOrdersActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        fetchOrders(userId);
    }

    private void fetchOrders(int userId) {
        Call<OwnerOrdersResponse> call = apiService.getOwnerOrders(userId); // API should fetch orders where user.role=owner
        call.enqueue(new Callback<OwnerOrdersResponse>() {
            @Override
            public void onResponse(Call<OwnerOrdersResponse> call, Response<OwnerOrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    orderList = new ArrayList<>();
                    Date today = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                    for (Order order : response.body().getOrders()) {
                        String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "pending";
                        if ("rejected".equals(status)) continue;

                        if ("accepted".equals(status)) {
                            String dateStr = order.getOrderDate();
                            if (dateStr != null && !dateStr.isEmpty()) {
                                try {
                                    Date orderDate = sdf.parse(dateStr);
                                    if (orderDate != null && (today.getTime() - orderDate.getTime()) <= 24*60*60*1000L) {
                                        orderList.add(order);
                                    }
                                } catch (ParseException e) { e.printStackTrace(); }
                            }
                        } else {
                            orderList.add(order); // pending always
                        }
                    }

                    adapter = new OrdersAdapter(orderList, new OrdersAdapter.OnOrderActionListener() {
                        @Override
                        public void onAccept(Order order) {
                            updateOrderStatus(order, "accepted");
                        }

                        @Override
                        public void onReject(Order order) {
                            updateOrderStatus(order, "rejected");
                        }

                        @Override
                        public void onOrderClick(Order order) {
                            Intent intent = new Intent(OwnerHomeActivity.this, OwnerOrderDetailsActivity.class);
                            intent.putExtra("order_id", order.getOrderId());
                            startActivity(intent);
                        }
                    }, true); // ✅ true = owner/business → shows buttons

                    rvOrders.setAdapter(adapter);

                } else {
                    Toast.makeText(OwnerHomeActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerOrdersResponse> call, Throwable t) {
                Toast.makeText(OwnerHomeActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateOrderStatus(Order order, String status) {
        Call<OrderResponse> call = apiService.updateOrder(order.getOrderId(), status.toLowerCase());
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {
                    order.setStatus(status.toLowerCase());
                    if ("rejected".equalsIgnoreCase(status)) {
                        int index = orderList.indexOf(order);
                        if (index >= 0) { orderList.remove(index); adapter.notifyItemRemoved(index); }
                    } else {
                        adapter.notifyItemChanged(orderList.indexOf(order));
                    }
                    Toast.makeText(OwnerHomeActivity.this, "Order " + status, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(OwnerHomeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Bottom navigation using if-else
    private boolean handleBottomNav(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_owner_home) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_owner_products) {
            Intent intent = new Intent(this, OwnerProductsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_owner_orders) {
            Intent intent = new Intent(this, BusinessOrdersActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_owner_profile) {
            Intent intent = new Intent(this, OwnerProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        }

        return false;
    }
}
