package com.saveetha.orderly_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.saveetha.orderly_book.api.ApiClient;
import com.saveetha.orderly_book.api.ApiService;
import com.saveetha.orderly_book.api.LoginRequest;
import com.saveetha.orderly_book.api.LoginResponse;
import com.saveetha.orderly_book.api.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnOwner, btnCustomer;
    String roleSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnOwner = findViewById(R.id.btnOwner);
        btnCustomer = findViewById(R.id.btnCustomer);

        TextView tvGoToSignup = findViewById(R.id.tvGoToSignup);
        tvGoToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        btnOwner.setOnClickListener(v -> roleSelected = "owner");
        btnCustomer.setOnClickListener(v -> roleSelected = "customer");

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(roleSelected.isEmpty()){
                Toast.makeText(this, "Select role first", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password){
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.loginUser(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    LoginResponse loginResponse = response.body();
                    if("success".equals(loginResponse.getStatus())){
                        User user = loginResponse.getUser();
                        if(user.getRole().equals(roleSelected)){
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Save user ID in SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("customer_id", roleSelected.equals("customer") ? user.getId() : -1);
                            editor.putInt("owner_id", roleSelected.equals("owner") ? user.getId() : -1);
                            editor.putString("user_name", user.getName());
                            editor.apply();

                            if(roleSelected.equals("owner")){
                                Intent intent = new Intent(LoginActivity.this, OwnerHomeActivity.class);
                                intent.putExtra("user_id", user.getId());
                                intent.putExtra("user_name", user.getName());
                                startActivity(intent);
                                finish();

                            } else if(roleSelected.equals("customer")){
                                Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                                intent.putExtra("customer_id", user.getId());
                                intent.putExtra("user_name", user.getName());
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Role mismatch!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed! Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
