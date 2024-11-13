package com.example.crabquizz.Scripts.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import com.example.crabquizz.HomeScreen;
import com.example.crabquizz.ProfileScreen;
import com.example.crabquizz.QuestionScreen;
import com.example.crabquizz.R;
import com.example.crabquizz.SearchScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuNavigationClickController {
    private final Context context;
    private static final String PREF_NAME = "NavigationState";
    private static final String CURRENT_SCREEN_KEY = "CurrentScreen";

    public MenuNavigationClickController(Context context) {
        this.context = context;
    }

    private void saveCurrentScreen(int screenId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(CURRENT_SCREEN_KEY, screenId).apply();
    }

    private int getCurrentScreen() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(CURRENT_SCREEN_KEY, R.id.home);
    }

    public void setUpAndHandleBottomNavigationView(View rootView) {
        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottomNavigation);

        // Thiết lập selected item dựa trên giá trị đã lưu
        bottomNavigationView.setSelectedItemId(getCurrentScreen());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId != getCurrentScreen()) {
                switch (itemId) {
                    case R.id.home:
                        saveCurrentScreen(R.id.home);
                        context.startActivity(new Intent(context, HomeScreen.class));
                        return true;
                    case R.id.search:
                        saveCurrentScreen(R.id.search);
                        context.startActivity(new Intent(context, SearchScreen.class));
                        return true;
                    case R.id.question:
                        saveCurrentScreen(R.id.question);
                        context.startActivity(new Intent(context, QuestionScreen.class));
                        return true;
                    case R.id.profile:
                        saveCurrentScreen(R.id.profile);
                        context.startActivity(new Intent(context, ProfileScreen.class));
                        return true;
                    default:
                        return false;
                }
            }
            return true;
        });
    }
}