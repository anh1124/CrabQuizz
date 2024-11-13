package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


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
        initPackage();
        InitView();
        SetGreeting();
        SetupImage();

        // Khởi tạo controller và truyền context
        MenuNavigationClickController controller = new MenuNavigationClickController(this);
        // Truyền view của bottom navigation
        controller.setUpAndHandleBottomNavigationView(findViewById(R.id.bottomNavigation));
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
        imageView = findViewById(R.id.imageView);
    }
    private void SetGreeting() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        String timeOfDay = getTimeOfDay();
        String greeting = "Chào buổi " + timeOfDay;

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