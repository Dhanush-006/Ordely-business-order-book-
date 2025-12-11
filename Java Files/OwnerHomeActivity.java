package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Order;
import com.saveetha.orderly_book.api.OwnerOrdersResponse;
import com.saveetha.orderly_book.api.OrdersAdapter;

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
    private Button btnAccepted, btnRejected, btnDelivered, btnPending;
    private Button btnTodayIn, btnTodayOut;
    private RecyclerView rvOrders;
    private OrdersAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();
    private ApiService apiService;
    private int userId;
    private BottomNavigationView bottomNavigationOwner;

    private String currentFilter = "";

    private final ActivityResultLauncher<Intent> updateOrderLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean updated = result.getData().getBooleanExtra("status_updated", false);
                    if (updated) fetchOrders(userId);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        initViews();

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);
        fetchOrders(userId);
    }

    private void initViews() {
        btnMyProducts = findViewById(R.id.btnOwnerProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnBusinessOrders = findViewById(R.id.btnBusinessOrders);
        rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationOwner = findViewById(R.id.bottomNavigationOwner);
        btnAccepted = findViewById(R.id.btnAccepted);
        btnRejected = findViewById(R.id.btnRejected);
        btnDelivered = findViewById(R.id.btnDelivered);
        btnPending = findViewById(R.id.btnPending);
        btnTodayIn = findViewById(R.id.btnTodayIn);
        btnTodayOut = findViewById(R.id.btnTodayOut);

        bottomNavigationOwner.setOnItemSelectedListener(this::handleBottomNav);

        btnMyProducts.setOnClickListener(v ->
                startActivity(new Intent(this, OwnerProductsActivity.class).putExtra("user_id", userId)));
        btnAddProduct.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class).putExtra("user_id", userId)));
        btnBusinessOrders.setOnClickListener(v ->
                startActivity(new Intent(this, BusinessOrdersActivity.class).putExtra("user_id", userId)));

        // Filters
        btnAccepted.setOnClickListener(v -> applyFilter("accepted"));
        btnRejected.setOnClickListener(v -> applyFilter("rejected"));
        btnDelivered.setOnClickListener(v -> applyFilter("delivered"));
        btnPending.setOnClickListener(v -> applyFilter("pending"));

        // Today's In/Out buttons
        btnTodayIn.setOnClickListener(v -> filterTodaysOrders(false));
        btnTodayOut.setOnClickListener(v -> filterTodaysOrders(true));
    }

    private void fetchOrders(int userId) {
        apiService.getOwnerOrders(userId).enqueue(new Callback<OwnerOrdersResponse>() {
            @Override
            public void onResponse(Call<OwnerOrdersResponse> call, Response<OwnerOrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    allOrders = response.body().getOrders();

                    adapter = new OrdersAdapter(new ArrayList<>(allOrders), new OrdersAdapter.OnOrderActionListener() {
                        @Override
                        public void onAccept(Order order) {
                            Intent intent = new Intent(OwnerHomeActivity.this, UpdateOrderActivity.class);
                            intent.putExtra("order_id", order.getOrderId());
                            intent.putExtra("status", "accepted");
                            updateOrderLauncher.launch(intent);
                        }

                        @Override
                        public void onReject(Order order) {
                            Intent intent = new Intent(OwnerHomeActivity.this, RejectOrderActivity.class);
                            intent.putExtra("order_id", order.getOrderId());
                            updateOrderLauncher.launch(intent);
                        }

                        @Override
                        public void onOrderClick(Order order) {
                            Intent intent = new Intent(OwnerHomeActivity.this, OwnerOrderDetailsActivity.class);
                            intent.putExtra("order_id", order.getOrderId());
                            startActivity(intent);
                        }
                    }, true);

                    rvOrders.setAdapter(adapter);
                    updateTodayCounts();

                    if (!currentFilter.isEmpty()) applyFilter(currentFilter);

                } else {
                    Toast.makeText(OwnerHomeActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerOrdersResponse> call, Throwable t) {
                Toast.makeText(OwnerHomeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId > 0) fetchOrders(userId);
    }

    private void applyFilter(String status) {
        if (adapter == null) return;

        if (currentFilter.equals(status)) {
            currentFilter = "";
            resetButtonColors();
            adapter.updateList(allOrders);
        } else {
            currentFilter = status;
            applyButtonHighlight(status);

            List<Order> filtered = new ArrayList<>();
            for (Order order : allOrders) {
                if (order.getStatus() != null && order.getStatus().equalsIgnoreCase(status)) {
                    filtered.add(order);
                }
            }
            adapter.updateList(filtered);
        }
    }

    private void applyButtonHighlight(String selected) {
        resetButtonColors();
        int highlightColor = getResources().getColor(android.R.color.holo_blue_dark);

        switch (selected) {
            case "accepted":
                btnAccepted.setBackgroundTintList(android.content.res.ColorStateList.valueOf(highlightColor));
                break;
            case "rejected":
                btnRejected.setBackgroundTintList(android.content.res.ColorStateList.valueOf(highlightColor));
                break;
            case "delivered":
                btnDelivered.setBackgroundTintList(android.content.res.ColorStateList.valueOf(highlightColor));
                break;
            case "pending":
                btnPending.setBackgroundTintList(android.content.res.ColorStateList.valueOf(highlightColor));
                break;
        }
    }

    private void resetButtonColors() {
        btnAccepted.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_dark)));
        btnRejected.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        btnDelivered.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_dark)));
        btnPending.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray)));
    }

    private void updateTodayCounts() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        int inCount = 0, outCount = 0;

        for (Order order : allOrders) {
            String dateStr = order.getOrderDate();
            if (dateStr != null && dateStr.startsWith(today)) {
                String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "";
                if ("delivered".equals(status)) {
                    outCount++;
                } else if ("accepted".equals(status) || "pending".equals(status) || "rejected".equals(status)) {
                    inCount++;
                }
            }
        }

        btnTodayIn.setText("Today's In: " + inCount);
        btnTodayOut.setText("Today's Out: " + outCount);
    }

    private void filterTodaysOrders(boolean delivered) {
        if (adapter == null || allOrders == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        List<Order> filtered = new ArrayList<>();
        for (Order order : allOrders) {
            String dateStr = order.getOrderDate();
            String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "";

            if (dateStr != null && dateStr.startsWith(today)) {
                if (delivered && "delivered".equals(status)) {
                    filtered.add(order);
                } else if (!delivered && ("accepted".equals(status) || "pending".equals(status) || "rejected".equals(status))) {
                    filtered.add(order);
                }
            }
        }

        adapter.updateList(filtered);
        currentFilter = ""; // reset regular filter when showing today's orders
    }

    private boolean handleBottomNav(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_owner_home) {
            fetchOrders(userId);
            return true;
        } else if (id == R.id.nav_owner_products) {
            startActivity(new Intent(this, OwnerProductsActivity.class).putExtra("user_id", userId));
            return true;
        } else if (id == R.id.nav_owner_orders) {
            startActivity(new Intent(this, BusinessOrdersActivity.class).putExtra("user_id", userId));
            return true;
        } else if (id == R.id.nav_owner_profile) {
            startActivity(new Intent(this, OwnerProfileActivity.class).putExtra("user_id", userId));
            return true;
        }
        return false;
    }
}
