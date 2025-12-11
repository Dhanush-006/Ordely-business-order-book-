package com.saveetha.orderly_book;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.OrderResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectOrderActivity extends AppCompatActivity {

    private Spinner spinnerReason;
    private EditText etNote;
    private Button btnSubmit, btnCancel;
    private int orderId;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_order);

        // ✅ Match with correct XML IDs
        spinnerReason = findViewById(R.id.spinnerReason);
        etNote = findViewById(R.id.etRejectionNote);
        btnSubmit = findViewById(R.id.btnRejectSubmit);
        btnCancel = findViewById(R.id.btnRejectCancel);

        // ✅ Populate spinner
        String[] reasons = {
                "Select reason",
                "Out of stock",
                "Supplier delayed",
                "Customer request",
                "Pricing issue",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                reasons
        );
        spinnerReason.setAdapter(adapter);

        // ✅ API service init
        apiService = ApiClient.getClient().create(ApiService.class);

        // ✅ Get order ID
        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Rejecting order...");
        progressDialog.setCancelable(false);

        // ✅ Handle submit
        btnSubmit.setOnClickListener(v -> {
            String reason = spinnerReason.getSelectedItem().toString().trim();
            String note = etNote.getText().toString().trim();

            if (reason.equals("Select reason")) {
                Toast.makeText(this, "Please select a rejection reason", Toast.LENGTH_SHORT).show();
                return;
            }

            rejectOrder(orderId, reason, note);
        });

        // ✅ Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void rejectOrder(int orderId, String reason, String note) {
        progressDialog.show();

        // ✅ Make sure your ApiService has this:
        // @FormUrlEncoded
        // @POST("reject_order.php")
        // Call<OrderResponse> rejectOrder(@Field("order_id") int orderId, @Field("rejection_reason") String reason, @Field("rejection_note") String note);
        Call<OrderResponse> call = apiService.rejectOrder(orderId, reason, note);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    Toast.makeText(RejectOrderActivity.this, "Order rejected successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("status_updated", true);
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    Toast.makeText(RejectOrderActivity.this,
                            "Failed to reject: " + (response.body() != null ? response.body().getMessage() : "Unknown error"),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RejectOrderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
