package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.SignupRequest;
import com.saveetha.orderly_book.api.SignupResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnOwner, btnCustomer, btnSignup;
    private String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnOwner = findViewById(R.id.btnOwner);
        btnCustomer = findViewById(R.id.btnCustomer);
        btnSignup = findViewById(R.id.btnSignup);

        // Select Owner
        btnOwner.setOnClickListener(v -> {
            selectedRole = "owner";
            btnOwner.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_dark));
            btnCustomer.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        });

        // Select Customer
        btnCustomer.setOnClickListener(v -> {
            selectedRole = "customer";
            btnCustomer.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_dark));
            btnOwner.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        });

        // Sign Up
        btnSignup.setOnClickListener(v -> signupUser());
    }

    private void signupUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        SignupRequest request = new SignupRequest(name, email, password, selectedRole);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<SignupResponse> call = apiService.signupUser(request);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SignupResponse res = response.body();

                    if ("success".equals(res.getStatus())) {
                        Toast.makeText(SignupActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        // Go back to login screen
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Server Error. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
