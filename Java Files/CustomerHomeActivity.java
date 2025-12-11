package com.saveetha.orderly_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Order;
import com.saveetha.orderly_book.api.OrdersResponse;
import com.saveetha.orderly_book.api.OwnersAdapter;
import com.saveetha.orderly_book.api.OwnersResponse;
import com.saveetha.orderly_book.api.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity {

    private Button btnViewProducts, btnViewCompanies, btnPlaceOrder, btnShowOrdersPie;
    private TextView tvSectionTitle;
    private RecyclerView recyclerItems;
    private BottomNavigationView bottomNavigationCustomer;
    private PieChart pieChart;

    private ApiService apiService;
    private List<User> owners = new ArrayList<>();
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // Initialize UI
        btnViewProducts = findViewById(R.id.btnViewProducts);
        btnViewCompanies = findViewById(R.id.btnViewCompanies);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnShowOrdersPie = findViewById(R.id.btnShowOrdersPie);

        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        recyclerItems = findViewById(R.id.recyclerItems);
        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setVisibility(View.GONE);

        pieChart = findViewById(R.id.pieChartOrders);
        pieChart.setVisibility(View.GONE);

        bottomNavigationCustomer = findViewById(R.id.bottomNavigationCustomer);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Retrieve logged-in customer ID
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        customerId = prefs.getInt("customer_id", -1);

        if (customerId <= 0) {
            Toast.makeText(this, "Customer not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Button click listeners
        btnViewCompanies.setOnClickListener(v -> loadBusinessOwners());

        btnViewProducts.setOnClickListener(v -> {
            tvSectionTitle.setText("Available Products");
            tvSectionTitle.setVisibility(View.VISIBLE);
            recyclerItems.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            Toast.makeText(this, "Fetch products from DB here", Toast.LENGTH_SHORT).show();
        });

        btnPlaceOrder.setOnClickListener(v -> {
            tvSectionTitle.setText("Select Owner for Order");
            tvSectionTitle.setVisibility(View.VISIBLE);
            recyclerItems.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);

            if (owners.isEmpty()) {
                loadBusinessOwnersForOrder();
            } else {
                setupOwnersAdapterForOrder();
            }
        });

        btnShowOrdersPie.setOnClickListener(v -> {
            tvSectionTitle.setText("Orders Analysis");
            tvSectionTitle.setVisibility(View.VISIBLE);
            recyclerItems.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            fetchCustomerOrdersForPie();
        });

        bottomNavigationCustomer.setOnItemSelectedListener(this::handleBottomNav);
    }

    // Load owners for viewing
    private void loadBusinessOwners() {
        tvSectionTitle.setText("Registered Business Owners");
        recyclerItems.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);

        Call<OwnersResponse> call = apiService.getOwners();
        call.enqueue(new Callback<OwnersResponse>() {
            @Override
            public void onResponse(Call<OwnersResponse> call, Response<OwnersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    owners = response.body().getOwners();
                    OwnersAdapter adapter = new OwnersAdapter(owners, owner -> {
                        Intent intent = new Intent(CustomerHomeActivity.this, OwnerProductsCustomerActivity.class);
                        intent.putExtra("owner_id", owner.getId());
                        intent.putExtra("owner_name", owner.getName());
                        intent.putExtra("customer_id", customerId);
                        startActivity(intent);
                    });
                    recyclerItems.setAdapter(adapter);
                } else {
                    Toast.makeText(CustomerHomeActivity.this, "No owners found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnersResponse> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Load owners for placing order
    private void loadBusinessOwnersForOrder() {
        Call<OwnersResponse> call = apiService.getOwners();
        call.enqueue(new Callback<OwnersResponse>() {
            @Override
            public void onResponse(Call<OwnersResponse> call, Response<OwnersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    owners = response.body().getOwners();
                    setupOwnersAdapterForOrder();
                } else {
                    Toast.makeText(CustomerHomeActivity.this, "No owners found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnersResponse> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Setup adapter to open OwnerProductsCustomerActivity for order
    private void setupOwnersAdapterForOrder() {
        OwnersAdapter adapter = new OwnersAdapter(owners, owner -> {
            Intent intent = new Intent(CustomerHomeActivity.this, OwnerProductsCustomerActivity.class);
            intent.putExtra("customer_id", customerId);
            intent.putExtra("owner_id", owner.getId());
            intent.putExtra("owner_name", owner.getName());
            startActivity(intent);
        });
        recyclerItems.setAdapter(adapter);
    }

    private boolean handleBottomNav(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent(this, CustomerOrdersActivity.class);
            intent.putExtra("customer_id", customerId);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, CustomerProfileActivity.class);
            intent.putExtra("customer_id", customerId);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void fetchCustomerOrdersForPie() {
        Call<OrdersResponse> call = apiService.getCustomerOrders(customerId);
        call.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showPieChart(response.body().getOrders());
                } else {
                    Toast.makeText(CustomerHomeActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPieChart(List<Order> orders) {
        Map<String, Integer> ownerOrdersCount = new HashMap<>();
        for (Order order : orders) {
            String ownerName = order.getOwnerName();
            ownerOrdersCount.put(ownerName, ownerOrdersCount.getOrDefault(ownerName, 0) + 1);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ownerOrdersCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Orders by Owner");
        dataSet.setColors(new int[]{
                0xFFE57373, 0xFF64B5F6, 0xFF81C784, 0xFFFFB74D, 0xFFBA68C8, 0xFF4DD0E1
        });
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(0xFFFFFFFF);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);

        pieChart.invalidate();
    }
}
