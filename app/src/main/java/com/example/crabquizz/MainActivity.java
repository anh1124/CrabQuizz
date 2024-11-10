package com.example.crabquizz;

// Thêm imports cho Models
import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.example.crabquizz.Scripts.SessionManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView adminDataTextView;
    private DbContext dbContext;
    private SessionManager sessionManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //bắt đầu ở đây

        initPackage();
        initViews();

        //thử đăng nhập bằng token nếu có
        TryloginWithUsernameAndToken();
    }
    private void TryloginWithUsernameAndToken() {
        // Nếu có cả token và username
        if (sessionManager.isHaveToken() && sessionManager.isHaveUsername()) {
            String username = sessionManager.getUsername();
            String token = sessionManager.getToken();

            // Kiểm tra user với username và token
            dbContext.getUserByUsernameAndToken(username, token)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Tìm thấy user, tiếp tục lấy thông tin chi tiết
                        UserController.getInstance(sessionManager).getUserByUsername(username, new UserController.LoginCallback() {
                            @Override
                            public void onLoginSuccess(User user) {
                                // Lưu thông tin và chuyển sang màn hình chính
                                sessionManager.saveUserInfo(user);
                                startActivity(new Intent(MainActivity.this, HomeScreen.class));
                                finish();
                            }

                            @Override
                            public void onLoginFailed(String errorMessage) {
                                // Nếu thất bại, logout và chuyển màn hình chính
                                sessionManager.logoutUser();
                                startActivity(new Intent(MainActivity.this, HomeScreen.class));
                                finish();
                            }
                        });
                    } else {
                        // Nếu không tìm thấy user, logout và chuyển màn hình chính
                        sessionManager.logoutUser();
                        startActivity(new Intent(MainActivity.this, HomeScreen.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error verifying user", e);
                    // Handle error, e.g., show an error message or retry
                });
        } else {
            // Nếu không có token hoặc username, chuyển về màn hình chính
            startActivity(new Intent(MainActivity.this, HomeScreen.class));
            finish();
        }
    }


    // <editor-fold desc="Init">
//khai báo các thứ trên view
public void initViews()
{
    adminDataTextView = findViewById(R.id.adminDataTextView);
}
public void initPackage()
{
    // Khởi tạo DbContext
    dbContext = DbContext.getInstance();
    sessionManager = SessionManager.getInstance(this);
}

    // </editor-fold>



}