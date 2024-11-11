package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.SessionManager;
import com.example.crabquizz.Scripts.Models.User;

public class Register extends AppCompatActivity {

    private Button buttonBackToMainMenu, buttonRegister;
    private TextView textViewBtnGoLogin;
    private ImageView imageView;
    private EditText inputFullName, inputUserName, inputPassword, inputEmail;
    private CheckBox checkBoxIsTeacher;

    private DbContext dbContext;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        initPackage();

        
    }

    private void initPackage() {
        dbContext = DbContext.getInstance();
        sessionManager = SessionManager.getInstance(this);
    }

    private void initViews() {
        // Find views by ID
        buttonBackToMainMenu = findViewById(R.id.buttonbacktomainmenu);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewBtnGoLogin = findViewById(R.id.textViewBtnGoLogin);
        imageView = findViewById(R.id.imageView);
        inputFullName = findViewById(R.id.inputFullName);
        inputUserName = findViewById(R.id.inputUserName);
        inputPassword = findViewById(R.id.inputPassWord);
        inputEmail = findViewById(R.id.inputEmail);
        checkBoxIsTeacher = findViewById(R.id.checkBoxIsTeacher);

        // Optional: set click listeners for buttons and clickable text
        buttonBackToMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, HomeScreen.class);
            startActivity(intent);
        });

        buttonRegister.setOnClickListener(v -> {
            if (validateInputFields()) {
                String fullName = inputFullName.getText().toString().trim();
                String userName = inputUserName.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                boolean isTeacher = checkBoxIsTeacher.isChecked();
                UserController.getInstance(sessionManager).isEmailUnique(email)
                        .continueWith(task -> {
                            if (task.isSuccessful()) {
                                Boolean isUnique = task.getResult();
                                if (isUnique) {
                                    // Email duy nhất
                                    UserController.getInstance(sessionManager).register(fullName, userName, password, isTeacher ? "teacher" : "student", email, new UserController.RegisterCallback() {
                                        @Override
                                        public void onRegisterSuccess(User user) {
                                            // Nếu đăng ký thành công- chuyển sang màn hình đăng nhập

                                            GoLoginActive();

                                            Log.d("Register", "User registration successful");
                                        }

                                        @Override
                                        public void onRegisterFailed(String errorMessage) {
                                            // Registration failed, display the error message
                                            Log.e("Register", "User registration failed: " + errorMessage);

                                            //đăng ký thất bại
                                            //cái này mị không biết xử lý log kiểu gì :V
                                            //bắt user cứ đăng ký đến khi thành công thui

                                            // Display the error message to the user
                                            // For example, show a toast or set an error message on the corresponding input field
                                        }
                                    });
                                } else {
                                    // Email không duy nhất
                                    showToast("Email already exists");
                                }
                            } else {
                                // Xử lý nếu tác vụ thất bại
                                showToast("Registration failed");
                            }
                            return null; // Hoặc xử lý khác tùy yêu cầu
                        });



            }
        });

        textViewBtnGoLogin.setOnClickListener(v -> {
            GoLoginActive();
        });
    }
    private boolean validateInputFields() {
        boolean isValid = true;

        String fullName = inputFullName.getText().toString().trim();
        String userName = inputUserName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        boolean isTeacher = checkBoxIsTeacher.isChecked();

        // Validate FullName
        if (fullName.isEmpty() || fullName.length() < 3 || fullName.length() > 20) {
            inputFullName.setError("Full name must be between 3 and 20 characters");
            isValid = false;
        } else {
            inputFullName.setError(null);
        }

        // Validate UserName
        if (userName.isEmpty() || userName.length() < 3 || userName.length() > 20) {
            inputUserName.setError("User name must be between 3 and 20 characters");
            isValid = false;
        } else {
            inputUserName.setError(null);
        }

        // Validate Password
        if (password.isEmpty() || password.length() < 3 || password.length() > 20) {
            inputPassword.setError("Password must be between 3 and 20 characters");
            isValid = false;
        } else {
            inputPassword.setError(null);
        }

        // Validate Email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please enter a valid email address");
            isValid = false;
        } else {
            inputEmail.setError(null);
        }

        return isValid;
    }

    private void showToast(String message) {
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }
    public void GoLoginActive()
    {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

}