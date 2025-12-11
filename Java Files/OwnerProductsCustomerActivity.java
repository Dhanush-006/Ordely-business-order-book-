package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OrderRequest;
import com.saveetha.orderly_book.api.Product;
import com.saveetha.orderly_book.api.ProductsAdapter;
import com.saveetha.orderly_book.api.OwnerProductsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProductsCustomerActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private TextView tvOwnerName, tvTotal, etNotes;
    private Button btnPlaceOrder;
    private Spinner spinnerAddress;

    private ProductsAdapter productsAdapter;
    private ApiService apiService;

    private int ownerId, customerId;
    private double totalPrice = 0;

    // Dummy shipping addresses
    private List<String> shippingAddresses = new ArrayList<>();
    private List<Integer> shippingAddressIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_products_customer);

        // Initialize views
        rvProducts = findViewById(R.id.rvOwnerProducts);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        spinnerAddress = findViewById(R.id.spinnerAddress);
        etNotes = findViewById(R.id.etNotes);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        ownerId = getIntent().getIntExtra("owner_id", -1);
        customerId = getIntent().getIntExtra("customer_id", -1);

        if (ownerId <= 0 || customerId <= 0) {
            Toast.makeText(this, "Invalid IDs", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClient().create(ApiService.class);

        String ownerName = getIntent().getStringExtra("owner_name");
        tvOwnerName.setText(ownerName != null ? ownerName + "'s Products" : "Owner's Products");

        loadShippingAddresses();
        fetchProducts();

        btnPlaceOrder.setOnClickListener(v -> goToPayment());
    }

    private void loadShippingAddresses() {
        shippingAddresses.add("Home - 123 Street");
        shippingAddresses.add("Office - 456 Street");
        shippingAddresses.add("Other - 789 Street");
        shippingAddressIds.add(1);
        shippingAddressIds.add(2);
        shippingAddressIds.add(3);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, shippingAddresses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddress.setAdapter(adapter);
    }

    private void fetchProducts() {
        Call<OwnerProductsResponse> call = apiService.getOwnerProducts(ownerId);
        call.enqueue(new Callback<OwnerProductsResponse>() {
            @Override
            public void onResponse(Call<OwnerProductsResponse> call, Response<OwnerProductsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getProducts();
                    if (products != null && !products.isEmpty()) {
                        setupProductsRecycler(products);
                    } else {
                        Toast.makeText(OwnerProductsCustomerActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OwnerProductsCustomerActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerProductsResponse> call, Throwable t) {
                Toast.makeText(OwnerProductsCustomerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupProductsRecycler(List<Product> products) {
        productsAdapter = new ProductsAdapter(products);

        productsAdapter.setOnQuantityChangeListener(() -> {
            totalPrice = productsAdapter.getTotal();
            tvTotal.setText("Total: ₹" + totalPrice);
        });

        rvProducts.setAdapter(productsAdapter);
    }

    private void goToPayment() {
        if (productsAdapter == null) return;

        List<OrderRequest.ProductOrder> selectedProducts = new ArrayList<>();
        String notes = etNotes.getText().toString().trim();
        int selectedPosition = spinnerAddress.getSelectedItemPosition();

        if (selectedPosition < 0 || selectedPosition >= shippingAddresses.size()) {
            Toast.makeText(this, "Please select a shipping address", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedAddress = shippingAddresses.get(selectedPosition);
        int selectedAddressId = shippingAddressIds.get(selectedPosition);

        for (Product p : productsAdapter.getProducts()) {
            int qty = productsAdapter.getQuantity(p.getId());
            if (qty > 0) {
                selectedProducts.add(new OrderRequest.ProductOrder(
                        p.getId(), qty, notes, selectedAddressId
                ));
            }
        }

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Select at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Pass data to PaymentActivity without placing order yet
        Intent intent = new Intent(OwnerProductsCustomerActivity.this, PaymentActivity.class);
        intent.putExtra("customerId", customerId);
        intent.putExtra("ownerId", ownerId);
        intent.putExtra("totalAmount", totalPrice);
        intent.putExtra("notes", notes);
        intent.putExtra("shippingAddress", selectedAddress);

        // Pass selected products (must implement Serializable in ProductOrder)
        intent.putExtra("selectedProducts", new ArrayList<>(selectedProducts));

        startActivity(intent);
    }
}
