package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.example.crabquizz.Scripts.Controller.SessionManager;

import java.util.regex.Pattern;


public class Login extends AppCompatActivity {
    private DbContext dbContext;
    private SessionManager sessionManager;

    public Button buttonBackToMainMenu, buttonlogin;
    public TextInputEditText TextInputEditTextPassword, TextInputEditTextUserName;
    public TextView textViewBtnGoRegister;
    public CheckBox checkBoxAutoLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initPackage();
        setupInputFields();
    }
    private void setupInputFields() {
        // Cấu hình cho username field
        TextInputEditTextUserName.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD |
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        TextInputEditTextUserName.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);

        // Cấu hình cho password field
        TextInputEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD |
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        TextInputEditTextPassword.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);
    }

    private boolean validateInputFields() {
        boolean isValid = true;
        String username = TextInputEditTextUserName.getText().toString().trim();
        String password = TextInputEditTextPassword.getText().toString().trim();

        // Validate Username
        if (username.isEmpty() || username.length() < 3 || username.length() > 20) {
            TextInputEditTextUserName.setError("User name must be between 3 and 20 characters");
            isValid = false;
        } else if (!username.matches("^[a-zA-Z0-9_-]*$")) {
            TextInputEditTextUserName.setError("Username can only contain letters, numbers, underscores and hyphens");
            isValid = false;
        } else {
            TextInputEditTextUserName.setError(null);
        }

        // Validate Password
        if (password.isEmpty() || password.length() < 3 || password.length() > 20) {
            TextInputEditTextPassword.setError("Password must be between 3 and 20 characters");
            isValid = false;
        } else if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_\\-+=\\[\\]{}|:;<>,.?/~]*$")) {
            TextInputEditTextPassword.setError("Password contains invalid characters");
            isValid = false;
        } else {
            TextInputEditTextPassword.setError(null);
        }

        return isValid;
    }


    private void initPackage() {
        dbContext = DbContext.getInstance();
        sessionManager = SessionManager.getInstance(this);
    }


    private void initViews() {
        buttonBackToMainMenu = findViewById(R.id.buttonbacktomainmenu);
        buttonlogin = findViewById(R.id.buttonlogin);
        TextInputEditTextPassword = findViewById(R.id.TextInputEditTextPassword);
        TextInputEditTextUserName = findViewById(R.id.TextInputEditTextUserName);
        textViewBtnGoRegister = findViewById(R.id.textViewBtnGoLogin);
        checkBoxAutoLogin = findViewById(R.id.checkBoxAutoLogin);

        buttonBackToMainMenu.setOnClickListener(v -> GoHomeActive());
        buttonlogin.setOnClickListener(v -> DoLoginAction());
        textViewBtnGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private void GoHomeActive() {
        //TransitionFragemt.initializeMenuNavigation(this, getSupportFragmentManager(), findViewById(R.id.fragment_home));
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void DoLoginAction()
    {
        String username = TextInputEditTextUserName.getText().toString().trim();
        String password = TextInputEditTextPassword.getText().toString().trim();

        if(validateInputFields())
        {
            UserController.getInstance(this.sessionManager).loginWithUsernameAndPassword(username, password, new UserController.Callback() {
                @Override
                public void onSuccess(User user) {
                    CallWhenLoginSuccess();
                }

                @Override
                public void onFailed(String errorMessage) {
                    CallWhenLoginFalse();
                }
            });
        }
    }


    public void CallWhenLoginSuccess() {
        //tạo 1 cái currentUser
        User currentUser = sessionManager.getUserSession().getUser();
        Log.d("CallWhenLoginSuccess", "caller: currentUser.role" + currentUser.getRole() );
        // ghi  đè curentUser vào tempUser
        sessionManager.saveTEMPUserInfo(currentUser);
        // Log thông tin người dùng
        sessionManager.showLogUserData();
        //lưu lại thông tin vào sharedPreferences nếu autoLogin được chọn
        if (checkBoxAutoLogin.isChecked()) {
            DoAutoLogin(currentUser);
        }
        GoHomeActive();
    }

    public void DoAutoLogin(User user) {
        boolean autoLoginChecked = checkBoxAutoLogin.isChecked();
        if (autoLoginChecked) {
            // Nếu autoLoginChecked Me được chọn, lưu thông tin người dùng vào SharedPreferences
            sessionManager.SaveLoginSession(
                    user.getUsername(),
                    user.getFullName(),
                    user.getToken(),
                    user.getRole(),
                    true
            );
            Log.d("Login", "User info saved in SharedPreferences for auto-login.");
        } else {
            // Nếu Remember Me không được chọn, xóa thông tin người dùng khỏi SharedPreferences
            sessionManager.logoutUser();
            Log.d("Login", "User info cleared from SharedPreferences.");
        }
    }

    public void CallWhenLoginFalse()
    {
        buttonlogin.setError("Worong username or password");
    }

}