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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//active này sẽ khởi chạy đầu tiên
//chức năng là kiểm tra trong sharedPreferences xem có token và username có không
//nếu có thì thử đăng nhập
//


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

        //thử đăng nhập bằng token nếu có trong sharedPreferences
       TryloginWithUsernameAndTokenSharedPreferences();
    }
    private void TryloginWithUsernameAndTokenSharedPreferences() {
        // Nếu có cả token và username
        if (sessionManager.isHaveToken() && sessionManager.isHaveUsername()) {
            String username = sessionManager.getUsername();
            String token = sessionManager.getToken();

            Toast.makeText(MainActivity.this,
                    "Đang kiểm tra thông tin đăng nhập...",
                    Toast.LENGTH_SHORT).show();

            // Kiểm tra user với username và token
            dbContext.getUserByUsernameAndToken(username, token)
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Tìm thấy user, tiếp tục lấy thông tin chi tiết
                            UserController.getInstance(sessionManager).getUserByUsername(username, new UserController.LoginCallback() {
                                @Override
                                public void onLoginSuccess(User user) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity.this,
                                                "Đăng nhập thành công! Xin chào " + user.getFullName(),
                                                Toast.LENGTH_SHORT).show();
                                    });

                                    sessionManager.saveUserInfo(user);
                                    sessionManager.createLoginSession(user.getUsername(), user.getFullName(), user.getToken(), user.getToken());
                                    startActivity(new Intent(MainActivity.this, HomeScreen.class));
                                    finish();
                                }

                                @Override
                                public void onLoginFailed(String errorMessage) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity.this,
                                                "Đăng nhập thất bại: " + errorMessage,
                                                Toast.LENGTH_SHORT).show();
                                    });
                                    sessionManager.clearUserSession();
                                    sessionManager.logoutUser();
                                    startActivity(new Intent(MainActivity.this, HomeScreen.class));
                                    finish();
                                }
                            });
                        } else {
                            // Không tìm thấy user
                            Toast.makeText(MainActivity.this,
                                    "Không tìm thấy thông tin đăng nhập",
                                    Toast.LENGTH_SHORT).show();

                            sessionManager.logoutUser();
                            startActivity(new Intent(MainActivity.this, HomeScreen.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Error verifying user", e);
                        Toast.makeText(MainActivity.this,
                                "Lỗi khi kiểm tra thông tin: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        sessionManager.logoutUser();
                        startActivity(new Intent(MainActivity.this, HomeScreen.class));
                        finish();
                    });
        } else {
            // Không có token hoặc username
            Toast.makeText(MainActivity.this,
                    "Chưa có thông tin đăng nhập",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this, HomeScreen.class));
            finish();
        }
    }


    // <editor-fold desc="Init">
//khai báo các thứ trên view
public void initViews()
{

}
public void initPackage()
{
    // Khởi tạo DbContext
    dbContext = DbContext.getInstance();
    sessionManager = SessionManager.getInstance(this);
}

    // </editor-fold>




}