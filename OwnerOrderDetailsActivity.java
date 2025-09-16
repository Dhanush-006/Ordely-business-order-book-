package com.saveetha.orderly_book;

import android.os.Bundle;
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
    private TextView tvTotalPrice;
    private ApiService apiService;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
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
                if (response.isSuccessful() && response.body() != null &&
                        "success".equals(response.body().getStatus())) {

                    OrderDetailsAdapter adapter = new OrderDetailsAdapter(response.body().getItems());
                    rvOrderItems.setAdapter(adapter);

                    tvTotalPrice.setText("Total: ₹ " + response.body().getTotal_price());

                } else {
                    Toast.makeText(OwnerOrderDetailsActivity.this, "No details found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                Toast.makeText(OwnerOrderDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
