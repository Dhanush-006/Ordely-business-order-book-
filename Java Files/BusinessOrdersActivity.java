package com.saveetha.orderly_book;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.Order;
import com.saveetha.orderly_book.api.OwnerOrdersResponse;
import com.saveetha.orderly_book.api.OrdersAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessOrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrdersAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private SearchView searchView;
    private int ownerId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_orders);

        rvOrders = findViewById(R.id.rvBusinessOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchOrders);

        // Get logged-in user ID (acts as owner ID)
        ownerId = getIntent().getIntExtra("user_id", -1);
        if (ownerId <= 0) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchOrders(ownerId);

        // 🔍 Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterOrders(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOrders(newText);
                return false;
            }
        });
    }

    private void fetchOrders(int ownerId) {
        Call<OwnerOrdersResponse> call = apiService.getOwnerOrders(ownerId);
        call.enqueue(new Callback<OwnerOrdersResponse>() {
            @Override
            public void onResponse(Call<OwnerOrdersResponse> call, Response<OwnerOrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    orderList = response.body().getOrders();

                    adapter = new OrdersAdapter(orderList, new OrdersAdapter.OnOrderActionListener() {
                        @Override
                        public void onAccept(Order order) {
                            // Not used for owner view
                        }

                        @Override
                        public void onReject(Order order) {
                            // Not used for owner view
                        }

                        @Override
                        public void onOrderClick(Order order) {
                            Toast.makeText(BusinessOrdersActivity.this,
                                    "Clicked Order #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
                        }
                    }, true); // isBusinessOrder = true

                    rvOrders.setAdapter(adapter);

                } else {
                    Toast.makeText(BusinessOrdersActivity.this, "No orders found for this owner", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerOrdersResponse> call, Throwable t) {
                Toast.makeText(BusinessOrdersActivity.this,
                        "Failed to fetch orders: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterOrders(String query) {
        List<Order> filteredList = new ArrayList<>();
        for (Order order : orderList) {
            if ((order.getCustomerName() != null && order.getCustomerName().toLowerCase().contains(query.toLowerCase())) ||
                    String.valueOf(order.getOrderId()).contains(query)) {
                filteredList.add(order);
            }
        }
        if (adapter != null) adapter.updateList(filteredList);
    }
}
