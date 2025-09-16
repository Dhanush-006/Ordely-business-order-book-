package com.saveetha.orderly_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.saveetha.orderly_book.api.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvAnalysisTitle;
    private BottomNavigationView bottomNavigationCustomer;
    private CardView cardMyOrders, cardSettings, cardHelp, cardLogout, cardAnalysis;
    private PieChart pieChart;

    private int customerId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        // Initialize UI
        tvName = findViewById(R.id.tvCustomerName);
        tvEmail = findViewById(R.id.tvCustomerEmail);
        tvPhone = findViewById(R.id.tvCustomerPhone);
        tvAnalysisTitle = findViewById(R.id.tvAnalysisTitle);

        bottomNavigationCustomer = findViewById(R.id.bottomNavigationCustomer);

        // Cards
        cardMyOrders = findViewById(R.id.cardMyOrders);
        cardSettings = findViewById(R.id.cardSettings);
        cardHelp = findViewById(R.id.cardHelp);
        cardLogout = findViewById(R.id.cardLogout);
        cardAnalysis = findViewById(R.id.cardAnalysis);

        pieChart = findViewById(R.id.pieChartOrders);
        pieChart.setVisibility(View.GONE); // Initially hide pie chart

        apiService = ApiClient.getClient().create(ApiService.class);

        // Get customer_id from Intent
        customerId = getIntent().getIntExtra("customer_id", -1);
        if (customerId <= 0) {
            Toast.makeText(this, "Customer ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔥 Fetch customer details from DB
        fetchCustomerDetails(customerId);

        // Handle card clicks
        setupCardListeners();

        // Bottom navigation listener
        bottomNavigationCustomer.setOnItemSelectedListener(this::handleBottomNav);
    }

    private void fetchCustomerDetails(int id) {
        Call<User> call = apiService.getCustomerById(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User customer = response.body();
                    tvName.setText(customer.getName() != null ? customer.getName() : "N/A");
                    tvEmail.setText(customer.getEmail() != null ? customer.getEmail() : "N/A");
                    tvPhone.setText(customer.getContact() != null ? customer.getContact() : "N/A");
                } else {
                    Toast.makeText(CustomerProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(CustomerProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupCardListeners() {
        // Analysis Card - Show PieChart when clicked
        cardAnalysis.setOnClickListener(v -> {
            pieChart.setVisibility(View.VISIBLE);
            fetchCustomerOrdersForPie();
        });

        // Navigate to My Orders
        cardMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerOrdersActivity.class);
            intent.putExtra("customer_id", customerId);
            startActivity(intent);
        });

        // Settings
        cardSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );

        // Help & Support
        cardHelp.setOnClickListener(v ->
                startActivity(new Intent(this, HelpSupportActivity.class))
        );

        // Logout
        cardLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private boolean handleBottomNav(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            intent.putExtra("customer_id", customerId);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent(this, CustomerOrdersActivity.class);
            intent.putExtra("customer_id", customerId);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    // 🔥 Fetch orders to populate PieChart
    private void fetchCustomerOrdersForPie() {
        Call<OrdersResponse> call = apiService.getCustomerOrders(customerId);
        call.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body().getOrders();
                    showPieChart(orders);
                } else {
                    Toast.makeText(CustomerProfileActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                Toast.makeText(CustomerProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPieChart(List<Order> orders) {
        Map<String, Integer> ownerOrdersCount = new HashMap<>();
        for (Order order : orders) {
            String ownerName = order.getOwnerName(); // Ensure Order model has ownerName
            ownerOrdersCount.put(ownerName, ownerOrdersCount.getOrDefault(ownerName, 0) + 1);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ownerOrdersCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Orders by Owner");
        dataSet.setColors(new int[]{0xFFE57373, 0xFF64B5F6, 0xFF81C784, 0xFFFFB74D, 0xFFBA68C8, 0xFF4DD0E1});
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

        pieChart.invalidate(); // Refresh chart
    }
}
