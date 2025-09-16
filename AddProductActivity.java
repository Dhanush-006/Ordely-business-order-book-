package com.saveetha.orderly_book;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.GenericResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {

    EditText etProductName, etProductPrice;
    Button btnAddProduct;
    int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        ownerId = getIntent().getIntExtra("owner_id", -1);

        btnAddProduct.setOnClickListener(v -> {
            String name = etProductName.getText().toString().trim();
            String priceStr = etProductPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            addProduct(name, price);
        });
    }

    private void addProduct(String name, double price) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<GenericResponse> call = apiService.addProduct(ownerId, name, price);

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    Toast.makeText(AddProductActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        finish(); // go back to previous screen
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
