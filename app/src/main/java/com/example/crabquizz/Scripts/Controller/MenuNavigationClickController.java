package com.example.crabquizz.Scripts.Controller;

import android.app.Activity;
import android.app.ActivityOptions;
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

    public void setUpAndHandleBottomNavigationView(View rootView) {
        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottomNavigation);

        // Set selected tab dựa theo màn hình hiện tại
        if (context instanceof HomeScreen) {
            bottomNavigationView.setSelectedItemId(R.id.home);
            saveCurrentScreen(R.id.home);
        } else if (context instanceof SearchScreen) {
            bottomNavigationView.setSelectedItemId(R.id.search);
            saveCurrentScreen(R.id.search);
        } else if (context instanceof QuestionScreen) {
            bottomNavigationView.setSelectedItemId(R.id.question);
            saveCurrentScreen(R.id.question);
        } else if (context instanceof ProfileScreen) {
            bottomNavigationView.setSelectedItemId(R.id.profile);
            saveCurrentScreen(R.id.profile);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            switch (itemId) {
                case R.id.home:
                    if (!(context instanceof HomeScreen)) {
                        intent = new Intent(context, HomeScreen.class);
                        saveCurrentScreen(R.id.home);
                        addTransitionAnimation(intent);
                    }
                    break;
                case R.id.search:
                    if (!(context instanceof SearchScreen)) {
                        intent = new Intent(context, SearchScreen.class);
                        saveCurrentScreen(R.id.search);
                        addTransitionAnimation(intent);
                    }
                    break;
                case R.id.question:
                    if (!(context instanceof QuestionScreen)) {
                        intent = new Intent(context, QuestionScreen.class);
                        saveCurrentScreen(R.id.question);
                        addTransitionAnimation(intent);
                    }
                    break;
                case R.id.profile:
                    if (!(context instanceof ProfileScreen)) {
                        intent = new Intent(context, ProfileScreen.class);
                        saveCurrentScreen(R.id.profile);
                        addTransitionAnimation(intent);
                    }
                    break;
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    activity.finish();
                }
            }
            return true;
        });
    }

    private void addTransitionAnimation(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
            intent.putExtra("transition", true);
        }
    }

    private void saveCurrentScreen(int screenId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(CURRENT_SCREEN_KEY, screenId).apply();
    }
}