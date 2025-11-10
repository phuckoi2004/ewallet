package com.example.ewallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvBalance;
    private Button btnLogout;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        //tvBalance = findViewById(R.id.tvBalance);
        btnLogout = findViewById(R.id.btnLogout);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
    }

    private void loadUserData() {
        if (username != null) {
            DatabaseHelper.User user = dbHelper.getUserByUsername(username);
            if (user != null) {
                String displayName = user.getFullName() != null ? user.getFullName() : username;
                tvWelcome.setText("Xin chào, " + displayName + "! 👋");

                String balanceText = String.format("%,.0f VNĐ", user.getBalance());
                tvBalance.setText(balanceText);
            }
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("EWalletPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginOtherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}