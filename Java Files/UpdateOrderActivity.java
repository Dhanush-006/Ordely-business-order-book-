package com.saveetha.orderly_book;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OrderResponse;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateOrderActivity extends AppCompatActivity {

    private Spinner spinnerStatus;
    private EditText etDeliveryDate, etDestination, etTrackingStage;
    private Button btnUpdateOrder;
    private int orderId;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        etDeliveryDate = findViewById(R.id.etDeliveryDate);
        etDestination = findViewById(R.id.etDestination);
        etTrackingStage = findViewById(R.id.etTrackingStage);
        btnUpdateOrder = findViewById(R.id.btnUpdateOrder);

        apiService = ApiClient.getClient().create(ApiService.class);

        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Date picker for delivery date
        etDeliveryDate.setFocusable(false);
        etDeliveryDate.setOnClickListener(v -> showDatePicker());

        btnUpdateOrder.setOnClickListener(v -> updateOrder());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = selectedYear + "-" +
                            String.format("%02d", (selectedMonth + 1)) + "-" +
                            String.format("%02d", selectedDay);
                    etDeliveryDate.setText(selectedDate);
                },
                year, month, day);

        datePicker.show();
    }

    private void updateOrder() {
        String status = spinnerStatus.getSelectedItem().toString().toLowerCase();
        String deliveryDate = etDeliveryDate.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String trackingStage = etTrackingStage.getText().toString().trim();

        if (status.isEmpty()) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("order_id", orderId);
        body.put("status", status);
        if (!deliveryDate.isEmpty()) body.put("delivery_date", deliveryDate);
        if (!destination.isEmpty()) body.put("destination", destination);
        if (!trackingStage.isEmpty()) body.put("tracking_stage", trackingStage);

        Call<OrderResponse> call = apiService.updateOrderDetails(body);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse body = response.body();
                    Toast.makeText(UpdateOrderActivity.this, body.getMessage(), Toast.LENGTH_SHORT).show();
                    if ("success".equalsIgnoreCase(body.getStatus())) {
                        getIntent().putExtra("status_updated", true);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                } else {
                    Toast.makeText(UpdateOrderActivity.this, "Unexpected server response", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(UpdateOrderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}