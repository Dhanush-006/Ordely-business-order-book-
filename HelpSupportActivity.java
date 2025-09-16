package com.saveetha.orderly_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HelpSupportActivity extends AppCompatActivity {

    private CardView cardFAQ, cardContactUs, cardFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        cardFAQ = findViewById(R.id.cardFAQ);
        cardContactUs = findViewById(R.id.cardContactUs);
        cardFeedback = findViewById(R.id.cardFeedback);

        // Card click listeners
        cardFAQ.setOnClickListener(v -> {
            Toast.makeText(this, "Open FAQ Page", Toast.LENGTH_SHORT).show();
            // Start FAQ Activity if you have one
            // startActivity(new Intent(this, FAQActivity.class));
        });

        cardContactUs.setOnClickListener(v -> {
            Toast.makeText(this, "Open Contact Us", Toast.LENGTH_SHORT).show();
            // Start ContactUs Activity if you have one
            // startActivity(new Intent(this, ContactUsActivity.class));
        });

        cardFeedback.setOnClickListener(v -> {
            Toast.makeText(this, "Open Feedback Form", Toast.LENGTH_SHORT).show();
            // Start Feedback Activity if you have one
            // startActivity(new Intent(this, FeedbackActivity.class));
        });
    }
}
