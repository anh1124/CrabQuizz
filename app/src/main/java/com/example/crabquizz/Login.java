package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.example.crabquizz.Scripts.SessionManager;

public class Login extends AppCompatActivity {


    private DbContext dbContext;
    private SessionManager sessionManager;

    public Button buttonBackToMainMenu, buttonlogin;
    public EditText TextInputEditTextPassword, TextInputEditTextUserName;
    public TextView textViewBtnGoRegister;
    public CheckBox checkBoxRememberMe;

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

    }

    private void initPackage() {
        dbContext = DbContext.getInstance();
        sessionManager = SessionManager.getInstance(this);
    }

    private void initViews() {
        buttonBackToMainMenu = findViewById(R.id.buttonbacktomainmenu);
        buttonlogin =findViewById(R.id.buttonlogin);
        TextInputEditTextPassword =findViewById(R.id.TextInputEditTextPassword);
        TextInputEditTextUserName =findViewById(R.id.TextInputEditTextUserName);
        textViewBtnGoRegister = findViewById(R.id.textViewBtnGoLogin);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);

        buttonBackToMainMenu.setOnClickListener(v -> {
            GoHomeActive();
        });
        buttonlogin.setOnClickListener(v -> {
            DoLoginAction();
        });
        textViewBtnGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private void GoHomeActive() {
        Intent intent = new Intent(Login.this, HomeScreen.class);
        startActivity(intent);
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

    private boolean validateInputFields() {
        boolean isValid = true;

        String username = TextInputEditTextUserName.getText().toString().trim();
        String password = TextInputEditTextPassword.getText().toString().trim();



        // Validate UserName
        if (username.isEmpty() || username.length() < 3 || username.length() > 20) {
            TextInputEditTextUserName.setError("User name must be between 3 and 20 characters");
            isValid = false;
        } else {
            TextInputEditTextUserName.setError(null);
        }

        // Validate Password
        if (password.isEmpty() || password.length() < 3 || password.length() > 20) {
            TextInputEditTextPassword.setError("Password must be between 3 and 20 characters");
            isValid = false;
        } else {
            TextInputEditTextPassword.setError(null);
        }

        return isValid;
    }

    public void CallWhenLoginSuccess() {
        //tạo 1 cái currentUser
        User currentUser = sessionManager.getUserSession().getUser();
        Log.d("CallWhenLoginSuccess", "caller: currentUser.role" + currentUser.getRole() );
        // ghi  đè curentUser vào tempUser
        sessionManager.saveTEMPUserInfo(currentUser);
        // Log thông tin người dùng
        sessionManager.showLogUserData();
        GoHomeActive();
    }



    /*
    đang làm hàm truyền token vào để lấy user ,sau đó truyền cái user này cho user temp để lấy data
    * */
    public void IsRememberMe(String token, User user) {
        boolean rememberMe = checkBoxRememberMe.isChecked();

        if (rememberMe) {
            // Nếu Remember Me được chọn, lưu thông tin người dùng vào SharedPreferences
            sessionManager.createLoginSession(
                    user.getUsername(),
                    user.getFullName(),
                    token,
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