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
import com.saveetha.orderly_book.api.OrderDetailsAdapter;
import com.saveetha.orderly_book.api.OrderDetailsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerOrderDetailsActivity extends AppCompatActivity {

    private RecyclerView rvOrderItems;
    private TextView tvTotalPrice, tvShippingAddress, tvPaymentMode, tvTransactionId, tvCustomerNotes;
    private ApiService apiService;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvPaymentMode = findViewById(R.id.tvPaymentMode);
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvCustomerNotes = findViewById(R.id.tvCustomerNotes);

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        orderId = getIntent().getIntExtra("order_id", -1);
        apiService = ApiClient.getClient().create(ApiService.class);

        if (orderId != -1) {
            fetchOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOrderDetails(int orderId) {
        apiService.getOrderDetails(orderId).enqueue(new Callback<OrderDetailsResponse>() {
            @Override
            public void onResponse(Call<OrderDetailsResponse> call, Response<OrderDetailsResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(OwnerOrderDetailsActivity.this, "No details found", Toast.LENGTH_SHORT).show();
                    return;
                }

                OrderDetailsResponse data = response.body();

                // Set RecyclerView
                if (data.getItems() != null) {
                    rvOrderItems.setAdapter(new OrderDetailsAdapter(data.getItems()));
                }

                // Total Price
                // Total Price
                tvTotalPrice.setText("Total: ₹ " + data.getTotal_price());


                // Shipping Address
                if (data.getShippingAddress() != null && !data.getShippingAddress().isEmpty()) {
                    tvShippingAddress.setText("Shipping Address: " + data.getShippingAddress());
                    tvShippingAddress.setVisibility(View.VISIBLE);
                } else {
                    tvShippingAddress.setVisibility(View.GONE);
                }

                // Payment Mode
                if (data.getPaymentMode() != null && !data.getPaymentMode().isEmpty()) {
                    tvPaymentMode.setText("Payment Mode: " + data.getPaymentMode());
                    tvPaymentMode.setVisibility(View.VISIBLE);
                } else {
                    tvPaymentMode.setVisibility(View.GONE);
                }

                // Transaction ID
                if (data.getTransactionId() != null && !data.getTransactionId().isEmpty()) {
                    tvTransactionId.setText("Transaction ID: " + data.getTransactionId());
                    tvTransactionId.setVisibility(View.VISIBLE);
                } else {
                    tvTransactionId.setVisibility(View.GONE);
                }

                // Customer Notes
                if (data.getNotes() != null && !data.getNotes().isEmpty()) {
                    tvCustomerNotes.setText("Customer Notes: " + data.getNotes());
                    tvCustomerNotes.setVisibility(View.VISIBLE);
                } else {
                    tvCustomerNotes.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                Toast.makeText(OwnerOrderDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
