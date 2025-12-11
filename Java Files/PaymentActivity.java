package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OrderRequest;
import com.saveetha.orderly_book.api.OrderResponse;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    private double totalAmount;
    private int customerId, ownerId;
    private String notes, shippingAddress;
    private List<OrderRequest.ProductOrder> selectedProducts;

    private ApiService apiService;
    private Spinner spinnerPaymentMode;
    private Button btnPayNow;
    private TextView tvAmount, tvPaymentSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        apiService = ApiClient.getClient().create(ApiService.class);

        // ✅ Get intent data
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        customerId = getIntent().getIntExtra("customerId", -1);
        ownerId = getIntent().getIntExtra("ownerId", -1);
        notes = getIntent().getStringExtra("notes");
        shippingAddress = getIntent().getStringExtra("shippingAddress");
        selectedProducts = (List<OrderRequest.ProductOrder>) getIntent().getSerializableExtra("selectedProducts");

        tvAmount = findViewById(R.id.tvAmount);
        tvPaymentSummary = findViewById(R.id.tvPaymentSummary);
        spinnerPaymentMode = findViewById(R.id.spinnerPaymentMode);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Display amount and summary
        tvAmount.setText("Amount: ₹" + totalAmount);
        tvPaymentSummary.setText("Notes: " + notes + "\nShipping: " + shippingAddress);

        // Payment mode dropdown
        String[] paymentModes = {"UPI", "Credit Card", "Debit Card", "Net Banking", "Cash on Delivery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, paymentModes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMode.setAdapter(adapter);

        btnPayNow.setOnClickListener(v -> simulatePayment());
    }

    private void simulatePayment() {
        String paymentMode = spinnerPaymentMode.getSelectedItem().toString();

        // ✅ Generate a unique transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Toast.makeText(this,
                "Payment successful via " + paymentMode + "\nTransaction ID: " + transactionId,
                Toast.LENGTH_LONG).show();

        placeOrderAfterPayment(paymentMode, transactionId);
    }

    private void placeOrderAfterPayment(String paymentMode, String transactionId) {
        if (selectedProducts == null || selectedProducts.isEmpty()) return;

        // ✅ Add transactionId & paymentMode to the request
        OrderRequest orderRequest = new OrderRequest(
                customerId,
                ownerId,
                selectedProducts,
                totalAmount,
                notes,
                shippingAddress,
                paymentMode,
                transactionId   // new field
        );

        apiService.placeOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(PaymentActivity.this, OrderSuccessActivity.class);
                    intent.putExtra("totalAmount", totalAmount);
                    intent.putExtra("notes", notes);
                    intent.putExtra("shippingAddress", shippingAddress);
                    intent.putExtra("transactionId", transactionId);
                    intent.putExtra("paymentMode", paymentMode);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Network error placing order", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
