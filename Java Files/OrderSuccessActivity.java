package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView txtSuccessMessage, txtOrderDetails;
    private Button btnGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        txtSuccessMessage = findViewById(R.id.txtSuccessMessage);
        txtOrderDetails = findViewById(R.id.txtOrderDetails);
        btnGoHome = findViewById(R.id.btnGoHome);

        // Get data from intent
        double totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        String notes = getIntent().getStringExtra("notes");
        String address = getIntent().getStringExtra("shippingAddress");

        // Set text
        txtSuccessMessage.setText("🎉 Order Placed Successfully!");
        txtOrderDetails.setText(
                "Total Amount: ₹" + totalAmount + "\n\n" +
                "Delivery Address: " + (address != null ? address : "N/A") + "\n\n" +
                "Notes: " + (notes != null ? notes : "No special instructions")
        );

        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, CustomerHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
