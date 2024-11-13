package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.crabquizz.Scripts.Controller.UserController;
import com.example.crabquizz.Scripts.Controller.SessionManager;

public class ProfileScreen extends AppCompatActivity {
    public Button buttonlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MenuNavigationClickController controller = new MenuNavigationClickController(this);
        controller.setUpAndHandleBottomNavigationView(findViewById(R.id.bottomNavigation));
        initViews();
        ShowLogoutBtn();

    }

    private void ShowLogoutBtn() {
        SessionManager.UserTEMPSession userSession = SessionManager.getInstance(this).getUserSession();

        if (userSession != null && userSession.getUser() != null) {
            String role = userSession.getUser().getRole();
            Log.d("ShowLogoutBtn", "Role: " + role);

            // Chỉ hiện nút nếu role là "teacher" hoặc "student"
            if (role.equals("teacher") || role.equals("student")) {
                setbuttonlogoutVisibility(true);
            } else {
                setbuttonlogoutVisibility(false);
            }
        } else {

            setbuttonlogoutVisibility(false);
        }
    }
    public void setbuttonlogoutVisibility(boolean isVisible) {
        if (isVisible) {
            buttonlogout.setVisibility(Button.VISIBLE); // Show the button
        } else {
            buttonlogout.setVisibility(Button.GONE); // Hide the button
        }
    }

    private void initViews() {
        buttonlogout = findViewById(R.id.buttonlogout);
        buttonlogout.setOnClickListener(v -> {
            Log.d("buttonlogout", "CALLER: ");
            UserController.getInstance(SessionManager.getInstance(this)).logout(new UserController.CallLogOut() {
                @Override
                public void GoLoginMenu() {
                    Intent intent = new Intent(ProfileScreen.this, Login.class);
                    startActivity(intent);
                }

            });

        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}