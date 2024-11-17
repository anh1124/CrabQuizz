package com.example.crabquizz.Scripts.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crabquizz.HomeFragment;
import com.example.crabquizz.ProfileFragment;
import com.example.crabquizz.QuestionCreateFragment;
import com.example.crabquizz.QuestionFragment;
import com.example.crabquizz.R;
import com.example.crabquizz.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuNavigationClickController {
    private static final String TAG = "MenuNavController";
    private final Context context;
    private final FragmentManager fragmentManager;
    private static final String PREF_NAME = "NavigationState";
    private static final String CURRENT_SCREEN_KEY = "CurrentScreen";

    private BottomNavigationView studentNavigation;
    private BottomNavigationView teacherNavigation;
    private SessionManager sessionManager;

    public MenuNavigationClickController(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.sessionManager = SessionManager.getInstance(context);
    }

    public void initializeNavigations(View studentNav, View teacherNav) {
        if (!(studentNav instanceof BottomNavigationView) || !(teacherNav instanceof BottomNavigationView)) {
            Log.e(TAG, "Invalid navigation views provided");
            return;
        }

        studentNavigation = (BottomNavigationView) studentNav;
        teacherNavigation = (BottomNavigationView) teacherNav;

        // Update navigation visibility based on user role
        setUpNavigationBasedOnUserRole();

        // Set up navigation listeners for both student and teacher roles
        setupStudentNavigation();
        setupTeacherNavigation();

        // Restore last selected screen
        restoreLastScreen();
    }

    // Method to determine and show navigation based on the user's role
    private void setUpNavigationBasedOnUserRole() {
        String userRole = sessionManager.getUserSession().getUser().getRole();
        // If the user is a teacher, show teacher navigation, otherwise show student navigation
        if ("teacher".equals(userRole)) {
            teacherNavigation.setVisibility(View.VISIBLE);
            studentNavigation.setVisibility(View.GONE);
        } else {
            studentNavigation.setVisibility(View.VISIBLE);
            teacherNavigation.setVisibility(View.GONE);
        }
    }

    private void setupStudentNavigation() {
        studentNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = getFragmentForStudentNavigation(itemId);

            if (fragment != null) {
                loadFragment(fragment);
                saveCurrentScreen(itemId);
                return true;
            }
            return false;
        });
    }

    private void setupTeacherNavigation() {
        teacherNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = getFragmentForTeacherNavigation(itemId);

            if (fragment != null) {
                loadFragment(fragment);
                saveCurrentScreen(itemId);
                return true;
            }
            return false;
        });
    }

    // Method to get corresponding fragment for student navigation
    private Fragment getFragmentForStudentNavigation(int itemId) {
        switch (itemId) {
            case R.id.home:
                return new HomeFragment();
            case R.id.search:
                return new SearchFragment();
            case R.id.question:
                return new QuestionFragment();
            case R.id.profile:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    // Method to get corresponding fragment for teacher navigation
    private Fragment getFragmentForTeacherNavigation(int itemId) {
        switch (itemId) {
            case R.id.home:
                return new HomeFragment();
            case R.id.myclass:
                return new QuestionFragment();
            case R.id.question:
                return new QuestionCreateFragment();
            case R.id.profile:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    private void restoreLastScreen() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int lastScreenId = prefs.getInt(CURRENT_SCREEN_KEY, R.id.home);

        if ("teacher".equals(sessionManager.getUserSession().getUser().getRole())) {
            teacherNavigation.setSelectedItemId(lastScreenId);
        } else {
            studentNavigation.setSelectedItemId(lastScreenId);
        }
    }

    private void loadFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: ", e);
        }
    }

    private void saveCurrentScreen(int screenId) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(CURRENT_SCREEN_KEY, screenId).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving screen state: ", e);
        }
    }
}
