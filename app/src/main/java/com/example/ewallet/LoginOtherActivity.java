package com.example.ewallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginOtherActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText edtEmail, edtPassword;
    private TextView tvForgotPassword, tvRegister;
    private MaterialButton btnLogin;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_other);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("EWalletPrefs", MODE_PRIVATE);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginOtherActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email hoặc số điện thoại");
            isValid = false;
        } else if (!isValidEmailOrPhone(email)) {
            tilEmail.setError("Email hoặc số điện thoại không hợp lệ");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }

        if (!isValid) return;

        // Chuẩn hóa số điện thoại nếu cần
        String username = normalizePhone(email);

        if (dbHelper.checkUser(username, password)) {
            // Lấy thông tin user từ database
            DatabaseHelper.User user = dbHelper.getUserByUsername(username);
            String displayName = "User";

            if (user != null && user.getFullName() != null && !user.getFullName().isEmpty()) {
                displayName = user.getFullName();
            } else {
                displayName = formatPhoneNumber(username);
            }

            // Lưu vào SharedPreferences
            prefs.edit()
                    .putString("last_username", username)
                    .putString("display_name", displayName)
                    .apply();

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginOtherActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            if (dbHelper.checkUsernameExists(username)) {
                tilPassword.setError("Mật khẩu không đúng");
                edtPassword.setText("");
                edtPassword.requestFocus();
            } else {
                tilEmail.setError("Tài khoản không tồn tại");
            }
        }
    }

    private boolean isValidEmailOrPhone(String input) {
        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            return true;
        }
        // Chấp nhận số bắt đầu bằng 0, +84, hoặc 84
        if (input.matches("^(0|\\+84|84)(3|5|7|8|9)[0-9]{8}$")) {
            return true;
        }
        return false;
    }

    private String normalizePhone(String input) {
        // Nếu là email thì trả về nguyên gốc
        if (input.contains("@")) {
            return input;
        }

        // Nếu bắt đầu bằng 0, chuyển thành +84
        if (input.startsWith("0") && input.matches("^0(3|5|7|8|9)[0-9]{8}$")) {
            return "+84" + input.substring(1);
        }

        // Nếu đã có +84 thì giữ nguyên
        return input;
    }

    private String formatPhoneNumber(String phone) {
        if (phone.startsWith("+84")) {
            phone = "0" + phone.substring(3);
        }
        if (phone.length() >= 10) {
            return phone.substring(0, 4) + "***" + phone.substring(7);
        }
        return phone;
    }
}