package com.example.ewallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextView tvUserName, tvNotMe, tvForgotPassword;
    private TextInputLayout tilPassword;
    private TextInputEditText edtPassword;
    private MaterialButton btnLogin;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private String savedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        loadSavedUsername();
        setupListeners();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvNotMe = findViewById(R.id.tvNotMe);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tilPassword = findViewById(R.id.tilPassword);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("EWalletPrefs", MODE_PRIVATE);
    }

    private void loadSavedUsername() {
        savedUsername = prefs.getString("last_username", null);

        if (savedUsername != null) {
            String displayName = prefs.getString("display_name", "User");
            tvUserName.setText(displayName);
        } else {
            Intent intent = new Intent(LoginActivity.this, LoginOtherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupListeners() {
        tvNotMe.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginOtherActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String password = edtPassword.getText().toString().trim();

        tilPassword.setError(null);

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        if (dbHelper.checkUser(savedUsername, password)) {
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", savedUsername);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            tilPassword.setError("Mật khẩu không đúng");
            edtPassword.setText("");
            edtPassword.requestFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (edtPassword != null) {
            edtPassword.setText("");
        }
    }
}