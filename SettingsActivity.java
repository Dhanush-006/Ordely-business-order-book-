package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SettingsActivity extends AppCompatActivity {

    private CardView cardProfile, cardNotifications, cardChangePassword, cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cardProfile = findViewById(R.id.cardProfile);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardChangePassword = findViewById(R.id.cardChangePassword);
        cardLogout = findViewById(R.id.cardLogout);

        // Card click listeners
        cardProfile.setOnClickListener(v -> Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show());

        cardNotifications.setOnClickListener(v -> Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());

        cardChangePassword.setOnClickListener(v -> Toast.makeText(this, "Change Password clicked", Toast.LENGTH_SHORT).show());

        cardLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
