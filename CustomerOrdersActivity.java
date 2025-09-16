package com.saveetha.orderly_book;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.CustomerOrdersResponse;
import com.saveetha.orderly_book.api.Order;
import com.saveetha.orderly_book.api.OrdersAdapter;
import com.saveetha.orderly_book.api.OrdersResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrdersActivity extends AppCompatActivity {

    private BarChart barChart;
    private RecyclerView recyclerOrders;
    private OrdersAdapter ordersAdapter;
    private ApiService apiService;

    private int customerId;
    private int ownerId; // Optional: if viewing orders for a specific owner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_orders);

        // Initialize views
        barChart = findViewById(R.id.barChartCustomerOrders);
        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient().create(ApiService.class);

        // Retrieve customer_id and owner_id
        customerId = getIntent().getIntExtra("customer_id", -1);
        ownerId = getIntent().getIntExtra("owner_id", -1);

        // Fallback to SharedPreferences if customer_id not passed via intent
        if (customerId <= 0) {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            customerId = prefs.getInt("customer_id", -1);
        }

        if (customerId <= 0) {
            Toast.makeText(this, "Invalid customer", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load bar chart if ownerId is available
        if (ownerId > 0) fetchCustomerOrdersBarChart();

        // Load all customer orders in RecyclerView
        fetchCustomerOrdersList();
    }

    // --------------------- Bar Chart ---------------------
    private void fetchCustomerOrdersBarChart() {
        apiService.getCustomerOrdersForOwner(ownerId, customerId)
                .enqueue(new Callback<CustomerOrdersResponse>() {
                    @Override
                    public void onResponse(Call<CustomerOrdersResponse> call, Response<CustomerOrdersResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            setupBarChart(response.body().getMonthlyTotals());
                        } else {
                            Toast.makeText(CustomerOrdersActivity.this, "No monthly purchase data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CustomerOrdersResponse> call, Throwable t) {
                        Toast.makeText(CustomerOrdersActivity.this, "Failed to load bar chart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupBarChart(List<Float> monthlyTotals) {
        if (monthlyTotals == null || monthlyTotals.isEmpty()) return;

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < monthlyTotals.size(); i++) {
            entries.add(new BarEntry(i + 1, monthlyTotals.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Purchases");
        dataSet.setColor(getResources().getColor(R.color.purple_500));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.invalidate();
    }

    // --------------------- Orders List ---------------------
    private void fetchCustomerOrdersList() {
        Call<OrdersResponse> call = apiService.getCustomerOrders(customerId);
        call.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    List<Order> orders = response.body().getOrders();
                    if (orders != null && !orders.isEmpty()) {
                        ordersAdapter = new OrdersAdapter(orders, null, false); // false = read-only view
                        recyclerOrders.setAdapter(ordersAdapter);
                    } else {
                        Toast.makeText(CustomerOrdersActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CustomerOrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                Toast.makeText(CustomerOrdersActivity.this, "Failed to load orders: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
