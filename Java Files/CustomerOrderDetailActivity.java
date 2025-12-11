package com.saveetha.orderly_book;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OrderDetailsResponse;
import com.saveetha.orderly_book.api.OrderDetailsResponse.OrderItem;
import com.saveetha.orderly_book.api.OrderDetailsAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvDeliveryDate, tvDestination, tvTrackingStage;
    private TextView tvRejectionReason, tvRejectionNote;
    private RecyclerView recyclerItems;
    private ApiService apiService;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_detail);

        // Bind views
        tvOrderId = findViewById(R.id.tvOrderId);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvDestination = findViewById(R.id.tvDestination);
        tvTrackingStage = findViewById(R.id.tvTrackingStage);
        tvRejectionReason = findViewById(R.id.tvRejectionReason);
        tvRejectionNote = findViewById(R.id.tvRejectionNote);

        recyclerItems = findViewById(R.id.recyclerItems);
        recyclerItems.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient().create(ApiService.class);

        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchOrderDetails(orderId);
    }

    private void fetchOrderDetails(int orderId) {
        apiService.getOrderDetails(orderId).enqueue(new Callback<OrderDetailsResponse>() {
            @Override
            public void onResponse(Call<OrderDetailsResponse> call, Response<OrderDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    OrderDetailsResponse order = response.body();

                    // Order Info
                    tvOrderId.setText("Order #" + order.getOrderId());
                    tvDeliveryDate.setText("Delivery Date: " + order.getDeliveryDate());
                    tvDestination.setText("Destination: " + order.getDestination());
                    tvTrackingStage.setText("Tracking: " + order.getTrackingStage());

                    // Order-level rejection
                    if (order.getRejectionReason() != null && !order.getRejectionReason().isEmpty()) {
                        tvRejectionReason.setText("Rejection Reason: " + order.getRejectionReason());
                        tvRejectionReason.setVisibility(View.VISIBLE);
                    } else {
                        tvRejectionReason.setVisibility(View.GONE);
                    }

                    if (order.getRejectionNote() != null && !order.getRejectionNote().isEmpty()) {
                        tvRejectionNote.setText("Rejection Note: " + order.getRejectionNote());
                        tvRejectionNote.setVisibility(View.VISIBLE);
                    } else {
                        tvRejectionNote.setVisibility(View.GONE);
                    }

                    // Items
                    List<OrderItem> items = order.getItems();
                    if (items != null && !items.isEmpty()) {
                        recyclerItems.setAdapter(new OrderDetailsAdapter(items));
                    } else {
                        Toast.makeText(CustomerOrderDetailActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CustomerOrderDetailActivity.this, "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                Toast.makeText(CustomerOrderDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
