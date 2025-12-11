package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OwnersAdapter;
import com.saveetha.orderly_book.api.ProductsAdapter;
import com.saveetha.orderly_book.api.User;
import com.saveetha.orderly_book.api.OwnersResponse;
import com.saveetha.orderly_book.api.Product;
import com.saveetha.orderly_book.api.OrderRequest;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity {

    private RecyclerView rvOwners, rvProducts;
    private List<User> ownersList = new ArrayList<>();
    private List<Product> productsList = new ArrayList<>();
    private ApiService apiService;

    private int customerId;
    private int selectedOwnerId = -1;

    private TextView tvTotal;
    private Button btnPlaceOrder;
    private LinearLayout addressNotesLayout;
    private Spinner spinnerAddress;
    private EditText etNotes;

    private ProductsAdapter productsAdapter;
    private List<String> savedAddresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        rvOwners = findViewById(R.id.rvOwners);
        rvProducts = findViewById(R.id.rvProducts);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        addressNotesLayout = findViewById(R.id.addressNotesLayout);
        spinnerAddress = findViewById(R.id.spinnerAddress);
        etNotes = findViewById(R.id.etNotes);

        rvOwners.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        customerId = getIntent().getIntExtra("customer_id", -1);
        if (customerId <= 0) {
            Toast.makeText(this, "Invalid Customer ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        fetchOwners();
        loadSavedAddresses();

        btnPlaceOrder.setOnClickListener(v -> openPayment());
    }

    private void fetchOwners() {
        apiService.getOwners().enqueue(new retrofit2.Callback<OwnersResponse>() {
            @Override
            public void onResponse(retrofit2.Call<OwnersResponse> call, retrofit2.Response<OwnersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ownersList.clear();
                    ownersList.addAll(response.body().getOwners());
                    OwnersAdapter adapter = new OwnersAdapter(ownersList, owner -> {
                        selectedOwnerId = owner.getId();
                        fetchOwnerProducts(selectedOwnerId);
                    });
                    rvOwners.setAdapter(adapter);
                } else {
                    Toast.makeText(PlaceOrderActivity.this, "No owners found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<OwnersResponse> call, Throwable t) {
                Toast.makeText(PlaceOrderActivity.this, "Failed to fetch owners", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchOwnerProducts(int ownerId) {
        apiService.getOwnerProducts(ownerId).enqueue(new retrofit2.Callback<com.saveetha.orderly_book.api.OwnerProductsResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.saveetha.orderly_book.api.OwnerProductsResponse> call,
                                   retrofit2.Response<com.saveetha.orderly_book.api.OwnerProductsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productsList = response.body().getProducts();
                    if (productsList != null && !productsList.isEmpty()) {
                        setupProductsRecycler(productsList);
                    } else {
                        Toast.makeText(PlaceOrderActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.saveetha.orderly_book.api.OwnerProductsResponse> call, Throwable t) {
                Toast.makeText(PlaceOrderActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupProductsRecycler(List<Product> products) {
        productsAdapter = new ProductsAdapter(products);
        productsAdapter.setOnQuantityChangeListener(() ->
                tvTotal.setText("Total: ₹" + productsAdapter.getTotal())
        );
        rvProducts.setAdapter(productsAdapter);

        rvProducts.setVisibility(View.VISIBLE);
        addressNotesLayout.setVisibility(View.VISIBLE);
        btnPlaceOrder.setVisibility(View.VISIBLE);
    }

    private void loadSavedAddresses() {
        savedAddresses.clear();
        savedAddresses.add("Home - 123, Street, City");
        savedAddresses.add("Office - 456, Street, City");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, savedAddresses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddress.setAdapter(adapter);
    }

    private void openPayment() {
        if (productsAdapter == null || selectedOwnerId <= 0) {
            Toast.makeText(this, "Please select an owner and products", Toast.LENGTH_SHORT).show();
            return;
        }

        List<OrderRequest.ProductOrder> selectedProducts =
                productsAdapter.getSelectedProducts(etNotes.getText().toString(), spinnerAddress.getSelectedItemPosition());

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Select at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount = productsAdapter.getTotal();

        // Save selected products for later (used in PaymentActivity)
        PaymentDummyData.setSelectedProducts(selectedProducts);

        Intent intent = new Intent(PlaceOrderActivity.this, PaymentActivity.class);
        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("customerId", customerId);
        intent.putExtra("ownerId", selectedOwnerId);
        intent.putExtra("notes", etNotes.getText().toString());
        intent.putExtra("shippingAddressId", spinnerAddress.getSelectedItemPosition());
        startActivity(intent);
    }
}
