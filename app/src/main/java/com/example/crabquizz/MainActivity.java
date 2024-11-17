package com.example.crabquizz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Models.AppSetup;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DbContext dbContext;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        try {
            initPackage();
            initViews();

            GetAppSetUp();
            //CleanUserSession();
            CheckAutoAndLoginIfAble();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Có lỗi xảy ra khi khởi động ứng dụng", Toast.LENGTH_SHORT).show();
        }
    }

    private void CleanUserSession() {
        try {
            if (sessionManager != null) {
                sessionManager.clearUserSessionInSessionManager();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing session: ", e);
        }
    }

    private void CheckAutoAndLoginIfAble() {
        try {
            if (sessionManager != null) {
                // Kiểm tra xem đã có user session chưa
                if (sessionManager.getUserSession() != null &&
                        sessionManager.getUserSession().getUser() != null) {
                    // Nếu đã có session, chuyển thẳng đến home
                    navigateToHomeFragment();
                }
                // Nếu chưa có session nhưng có auto login
                else if (sessionManager.isAutoLoginEnabled()) {
                    TryloginWithUsernameAndTokenSharedPreferences();
                }
                // Nếu không có gì thì tạo guest
                else {
                    createGuestUserSession();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in auto login check: ", e);
            createGuestUserSession();
        }
    }

    private void createGuestUserSession() {
        try {
            if (sessionManager != null) {
                User guestUser = new User(0, "Guest", "guest", null, "guess", null, null, null);
                sessionManager.saveGuessSession(guestUser);
                sessionManager.SaveLoginSession("guest", "Guest", null, "guess", false);

                Toast.makeText(this, "Chế độ khách đã được kích hoạt.", Toast.LENGTH_SHORT).show();
                navigateToHomeFragment();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating guest session: ", e);
            Toast.makeText(this, "Không thể tạo phiên khách", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHomeFragment() {
        try {
            Log.d(TAG, "Navigating to HomeFragment");

            // Create an instance of the HomeFragment
            HomeFragment homeFragment = new HomeFragment();

            // Begin fragment transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace the current fragment with the HomeFragment
            transaction.replace(R.id.fragment_container, homeFragment);

            // Commit the transaction
            transaction.commitAllowingStateLoss(); // Use commitAllowingStateLoss to avoid state loss errors

            // Set up bottom navigation after fragment replacement
            setupBottomNavigation();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to HomeFragment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupBottomNavigation() {
        // Find the BottomNavigationView for student and teacher
        View studentNav = findViewById(R.id.studentBottomNavigation);
        View teacherNav = findViewById(R.id.teacherBottomNavigation);

        if (studentNav != null && teacherNav != null) {
            // Create an instance of MenuNavigationClickController
            MenuNavigationClickController controller = new MenuNavigationClickController(
                    this, // Context (Activity)
                    getSupportFragmentManager() // FragmentManager for handling fragment transactions
            );

            // Initialize the bottom navigation view and pass both student and teacher navigation views
            controller.initializeNavigations(studentNav, teacherNav);
        } else {
            Log.e(TAG, "One or both BottomNavigationView not found");
        }
    }



    private void GetAppSetUp() {
        try {
            AppSetup.getInstance().Setup();
        } catch (Exception e) {
            Log.e(TAG, "Error in app setup: ", e);
        }
    }

    private void TryloginWithUsernameAndTokenSharedPreferences() {
        try {
            if (sessionManager != null && sessionManager.isHaveToken() && sessionManager.isHaveUsername()) {
                String username = sessionManager.getSharedPreferencesUsername();
                String token = sessionManager.getSharedPreferencesToken();

                UserController.getInstance(sessionManager).loginWithUsernameAndToken(username, token, new UserController.Callback() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "User logged in successfully");
                        navigateToHomeFragment();
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        Log.e(TAG, "Login failed: " + errorMessage);
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "Đăng nhập thất bại: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                            createGuestUserSession();
                        });
                    }
                });
            } else {
                Toast.makeText(this, "Chưa có thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                createGuestUserSession();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in login attempt: ", e);
            createGuestUserSession();
        }
    }

    private void initViews() {
        // Add view initialization if needed
    }

    private void initPackage() {
        try {
            dbContext = DbContext.getInstance();
            sessionManager = SessionManager.getInstance(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing packages: ", e);
        }
    }

    // Override onBackPressed nếu cần xử lý back navigation
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeFragment) {
            super.onBackPressed(); // Thoát app nếu đang ở HomeFragment
        } else {
            getSupportFragmentManager().popBackStack(); // Quay lại fragment trước đó
        }
    }
}