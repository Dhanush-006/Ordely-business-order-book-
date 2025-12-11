package com.saveetha.orderly_book;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Order;
import com.saveetha.orderly_book.api.OrdersResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrdersPieChartActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ApiService apiService;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_orders_pie);

        pieChart = findViewById(R.id.pieChartOrders);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Get logged-in customer ID
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        customerId = prefs.getInt("customer_id", -1);

        if (customerId <= 0) {
            Toast.makeText(this, "Invalid customer", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchCustomerOrders();
    }

    private void fetchCustomerOrders() {
        Call<OrdersResponse> call = apiService.getCustomerOrders(customerId);

        call.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getOrders() != null) {
                    List<Order> orders = response.body().getOrders();
                    if (orders.isEmpty()) {
                        Toast.makeText(CustomerOrdersPieChartActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                    } else {
                        showPieChart(orders);
                    }
                } else {
                    Toast.makeText(CustomerOrdersPieChartActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                Toast.makeText(CustomerOrdersPieChartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPieChart(List<Order> orders) {
        Map<String, Integer> ownerOrdersCount = new HashMap<>();
        for (Order order : orders) {
            String ownerName = order.getOwnerName(); // Make sure Order model has getOwnerName()
            ownerOrdersCount.put(ownerName, ownerOrdersCount.getOrDefault(ownerName, 0) + 1);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ownerOrdersCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Orders by Owner");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.YELLOW});
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);

        pieChart.invalidate(); // Refresh chart
    }
}
