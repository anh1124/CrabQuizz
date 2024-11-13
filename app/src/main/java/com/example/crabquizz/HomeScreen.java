package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.SessionManager;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class HomeScreen extends AppCompatActivity {
    private TextView tvGreeting;
    private Button btnLoginSignup;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitView();

        //nút btnLoginSignup được ẩn bằng hàm dưới
        SetGreeting();
        ShowLoginSignupButton();
        SetGreeting();
        SetupImage();
        // Khởi tạo controller và truyền context
        MenuNavigationClickController controller = new MenuNavigationClickController(this);
        // Truyền view của bottom navigation
        controller.setUpAndHandleBottomNavigationView(findViewById(R.id.bottomNavigation));
    }

    private void ShowLoginSignupButton() {
        SessionManager.UserTEMPSession userSession = SessionManager.getInstance(this).getUserSession();

        if (userSession != null && userSession.getUser() != null) {
            String role = userSession.getUser().getRole();
            Log.d("ShowLoginSignupButton", "Role: " + role);

            // Chỉ ẩn nút nếu role là "teacher" hoặc "student"
            if (role.equals("teacher") || role.equals("student")) {
                setLoginSignupButtonVisibility(false);
            } else {
                setLoginSignupButtonVisibility(true);
            }
        } else {
            // Nếu không có user session, hiển thị nút đăng nhập
            setLoginSignupButtonVisibility(true);
        }

    }

    private void SetupImage() {
        imageView.setImageResource(R.drawable.university_student_cap_mortar_board_and_diploma);
    }

    private void initPackage() {

    }

    public void setLoginSignupButtonVisibility(boolean isVisible) {
        if (isVisible) {
            btnLoginSignup.setVisibility(Button.VISIBLE); // Show the button
        } else {
            btnLoginSignup.setVisibility(Button.GONE); // Hide the button
        }
    }
    private void InitView()
    {
        tvGreeting = findViewById(R.id.textViewGreeting);
        btnLoginSignup = findViewById(R.id.btnLoginSignup);

        btnLoginSignup.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreen.this, Register.class);
            startActivity(intent);
        });
        imageView = findViewById(R.id.imageView);
    }
    private void SetGreeting() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        String timeOfDay = getTimeOfDay();
        String greeting = "Chào buổi " + timeOfDay;

        // Kiểm tra xem có user session không
        SessionManager.UserTEMPSession userTEMPSession = sessionManager.getUserSession();
        if (userTEMPSession != null && userTEMPSession.getUser() != null) {
            greeting += " " + userTEMPSession.getUser().getFullName();

        }
        else
        {
            //setLoginSignupButtonVisibility(true);
            greeting += " Guess";
        }
        tvGreeting.setText(greeting);
//         //Kiểm tra xem có user session không
//        SessionManager.UserSession userSession = sessionManager.getUserSession();
//        if (userSession != null && userSession.getUser() != null) {
//            greeting += " " + userSession.getUser().getFullName();
//        }
//        else
//        {
//            greeting += " Guess";
//        }
//        tvGreeting.setText(greeting);
    }

    private String getTimeOfDay() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int timeOfDay = c.get(java.util.Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return "sáng";
        }else if(timeOfDay >= 12 && timeOfDay < 18){
            return "chiều";
        }else{
            return "tối";
        }
    }
}