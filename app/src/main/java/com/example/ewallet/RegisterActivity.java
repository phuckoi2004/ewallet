package com.example.ewallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialCardView btnUploadAvatar;
    private ImageView ivAvatar;
    private TextInputLayout tilFullName, tilPhone, tilCity, tilAddress, tilPassword, tilConfirmPassword;
    private TextInputEditText edtFullName, edtPhone, edtCity, edtAddress, edtPassword, edtConfirmPassword;
    private Spinner spinnerGender, spinnerCountry;
    private MaterialButton btnRegister;

    private DatabaseHelper dbHelper;
    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupSpinners();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnUploadAvatar = findViewById(R.id.btnUploadAvatar);
        ivAvatar = findViewById(R.id.ivAvatar);
        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilCity = findViewById(R.id.tilCity);
        tilAddress = findViewById(R.id.tilAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtCity = findViewById(R.id.edtCity);
        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DatabaseHelper(this);
    }

    private void setupSpinners() {
        String[] genders = {"Chọn giới tính của bạn", "Nam", "Nữ", "Khác"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedGender = genders[position];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] countries = {"Việt Nam"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnUploadAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng upload ảnh đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String city = edtCity.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        tilFullName.setError(null);
        tilPhone.setError(null);
        tilCity.setError(null);
        tilAddress.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        boolean isValid = true;

        if (fullName.isEmpty()) {
            tilFullName.setError("Vui lòng nhập họ và tên");
            isValid = false;
        } else if (fullName.length() < 3) {
            tilFullName.setError("Tên phải có ít nhất 3 ký tự");
            isValid = false;
        }

        if (spinnerGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (phone.isEmpty()) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        } else if (!isValidVietnamesePhone(phone)) {
            tilPhone.setError("Số điện thoại không hợp lệ (10 số, bắt đầu 03/05/07/08/09)");
            isValid = false;
        }

        if (city.isEmpty()) {
            tilCity.setError("Vui lòng nhập tỉnh/thành phố");
            isValid = false;
        }

        if (address.isEmpty()) {
            tilAddress.setError("Vui lòng nhập địa chỉ chi tiết");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui lòng nhập lại mật khẩu");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            isValid = false;
        }

        if (!isValid) return;

        String username = "+84" + phone;

        if (dbHelper.checkUsernameExists(username)) {
            tilPhone.setError("Số điện thoại đã được đăng ký");
            return;
        }

        boolean success = dbHelper.insertUser(username, password, fullName, selectedGender, city + ", " + address);

        if (success) {
            Toast.makeText(this, "Tạo tài khoản thành công! 🎉", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(RegisterActivity.this, LoginOtherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidVietnamesePhone(String phone) {
        return phone.matches("^(03|05|07|08|09)[0-9]{8}$");
    }
}