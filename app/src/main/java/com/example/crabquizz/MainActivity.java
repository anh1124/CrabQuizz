package com.example.crabquizz;

// Thêm imports cho Models
import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.example.crabquizz.Scripts.SessionManager;
import com.example.crabquizz.Scripts.Models.AppSetup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

//active này sẽ khởi chạy đầu tiên
//chức năng là kiểm tra trong sharedPreferences xem có token và username có không
//nếu có thì thử đăng nhập
//


public class MainActivity extends AppCompatActivity {
    private DbContext dbContext;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initPackage();
        initViews();


        GetAppSetUp();//đã cật nhật ngày hết hạn token

        CheckAutoAndLoginIfAble();//kiểm tra biến auto loggin và thực hiện login nếu có thể

        /*
            khi chuyển sang khác ,sẽ có 1 thông báo là bạn chưa đăng nhập,vui lòng đăng nhập vì sau khi thoát sẽ mất hết data
        */
    }

    private void CheckAutoAndLoginIfAble() {
        //nếu tự động login được bật thì chạy hàm này
        if(SessionManager.getInstance(this).isAutoLoginEnabled())
        {
            TryloginWithUsernameAndTokenSharedPreferences();
        }
        else
        {

            //nếu autologin không được bật,bắt đầu với tài khoản guess của học sinh
            createGuestUserSession();
        }

    }
    private void createGuestUserSession() {
        User guestUser = new User(0,"Guest", "guest", null, "guess", null, null, null);
        sessionManager.saveGuessSession(guestUser);
        sessionManager.createLoginSession("guest", "Guest", null, "guess", false);

        Toast.makeText(MainActivity.this,
                "Chế độ khách đã được kích hoạt.",
                Toast.LENGTH_SHORT).show();

        startActivity(new Intent(MainActivity.this, HomeScreen.class));
        finish();
    }
    private void GetAppSetUp() {
        AppSetup.getInstance().Setup();
    }


    private void TryloginWithUsernameAndTokenSharedPreferences() {
        if (sessionManager.isHaveToken() && sessionManager.isHaveUsername()) {
            String username = sessionManager.getSharedPreferencesUsername();
            String token = sessionManager.getSharedPreferencesToken();

            UserController.getInstance(sessionManager).loginWithUsernameAndToken(username, token, new UserController.LoginCallback() {
                //2 cái cục override bên dưới là interface callback
                @Override
                public void onLoginSuccess(User user) {
                    // User logged in successfully
                    Log.d("MainActivity", "User logged in successfully");
                    startActivity(new Intent(MainActivity.this, HomeScreen.class));
                    finish();
                }

                @Override
                public void onLoginFailed(String errorMessage) {
                    // Login failed
                    Log.e("MainActivity", "Login failed: " + errorMessage);
                    Toast.makeText(MainActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Chưa có thông tin đăng nhập", Toast.LENGTH_SHORT).show();
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