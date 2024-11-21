package com.example.crabquizz.Scripts.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crabquizz.ScreenStudentClassFragment;
import com.example.crabquizz.ScreenTeacherClassFragment;
import com.example.crabquizz.HomeFragment;
import com.example.crabquizz.ProfileFragment;
import com.example.crabquizz.QuestionFragment;
import com.example.crabquizz.SearchFragment;
import com.example.crabquizz.R;
import com.example.crabquizz.StudentQuestionViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Controller quản lý điều hướng menu bottom navigation
 * Xử lý chuyển đổi màn hình và hiển thị menu dựa trên vai trò người dùng (giáo viên/học sinh)
 */
public class MenuNavigationClickController {
    // Constants for logging and preferences
    private static final String TAG = "MenuNavController";
    private static final String PREF_NAME = "NavigationState";
    private static final String CURRENT_SCREEN_KEY = "CurrentScreen";

    // Core dependencies
    private final Context context;
    private final FragmentManager fragmentManager;
    private final SessionManager sessionManager;

    // Navigation views for different user roles
    private BottomNavigationView studentNavigation;
    private BottomNavigationView teacherNavigation;

    /**
     * Khởi tạo controller với context và fragment manager
     */
    public MenuNavigationClickController(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.sessionManager = SessionManager.getInstance(context);
    }

    /**
     * Khởi tạo và thiết lập navigation views cho cả học sinh và giáo viên
     * @param studentNav View navigation của học sinh
     * @param teacherNav View navigation của giáo viên
     */
    public void initializeNavigations(View studentNav, View teacherNav) {
        if (!(studentNav instanceof BottomNavigationView) || !(teacherNav instanceof BottomNavigationView)) {
            Log.e(TAG, "Invalid navigation views provided");
            return;
        }

        studentNavigation = (BottomNavigationView) studentNav;
        teacherNavigation = (BottomNavigationView) teacherNav;

        setUpNavigationBasedOnUserRole();
        setupStudentNavigation();
        setupTeacherNavigation();
        restoreLastScreen();
    }

    /**
     * Thiết lập hiển thị navigation dựa trên vai trò người dùng
     */
    private void setUpNavigationBasedOnUserRole() {
        String userRole = sessionManager.getUserSession().getUser().getRole();
        boolean isTeacher = "teacher".equals(userRole);

        teacherNavigation.setVisibility(isTeacher ? View.VISIBLE : View.GONE);
        studentNavigation.setVisibility(isTeacher ? View.GONE : View.VISIBLE);
    }

    /**
     * Thiết lập listener cho navigation của học sinh
     */
    private void setupStudentNavigation() {
        studentNavigation.setOnItemSelectedListener(item -> {
            if (isCurrentFragment(getFragmentClassName(item.getItemId()))) {
                return false; // Không điều hướng nếu đang ở fragment hiện tại
            }
            Fragment fragment = getFragmentForStudentNavigation(item.getItemId());
            return handleNavigation(fragment, item.getItemId());
        });
    }

    /**
     * Thiết lập listener cho navigation của giáo viên
     */
    private void setupTeacherNavigation() {
        teacherNavigation.setOnItemSelectedListener(item -> {
            if (isCurrentFragment(getFragmentClassName(item.getItemId()))) {
                return false; // Không điều hướng nếu đang ở fragment hiện tại
            }
            Fragment fragment = getFragmentForTeacherNavigation(item.getItemId());
            return handleNavigation(fragment, item.getItemId());
        });
    }

    private String getFragmentClassName(int itemId) {
        switch (itemId) {
            case R.id.home: return "HomeFragment";
            case R.id.search: return "SearchFragment";
            case R.id.myclass: return "ScreenTeacherClassFragment";
            case R.id.question: return "QuestionFragment";
            case R.id.profile: return "ProfileFragment";
            default: return "";
        }
    }


    /**
     * Lấy fragment tương ứng với item được chọn trong menu học sinh
     */
    private Fragment getFragmentForStudentNavigation(int itemId) {
        switch (itemId) {
            case R.id.home: return new HomeFragment();
            case R.id.search: return new SearchFragment();
            case R.id.myclass: return new StudentQuestionViewFragment();
            case R.id.profile: return new ProfileFragment();
            default: return null;
        }
    }

    /**
     * Lấy fragment tương ứng với item được chọn trong menu giáo viên
     */
    private Fragment getFragmentForTeacherNavigation(int itemId) {
        switch (itemId) {
            case R.id.home: return new HomeFragment();
            case R.id.myclass: return new ScreenTeacherClassFragment();
            case R.id.question: return new QuestionFragment();
            case R.id.profile: return new ProfileFragment();
            default: return null;
        }
    }

    /**
     * Xử lý chung cho việc chuyển đổi fragment
     */
    private boolean handleNavigation(Fragment fragment, int itemId) {
        if (fragment != null) {
            loadFragment(fragment);
            saveCurrentScreen(itemId);
            return true;
        }
        return false;
    }

    /**
     * Khôi phục màn hình cuối cùng được chọn (mặc định là Home)
     */
    private void restoreLastScreen() {
        int homeScreenId = R.id.home;
        String userRole = sessionManager.getUserSession().getUser().getRole();

        if ("teacher".equals(userRole)) {
            teacherNavigation.setSelectedItemId(homeScreenId);
        } else {
            studentNavigation.setSelectedItemId(homeScreenId);
        }

        loadFragment(new HomeFragment());
    }

    /**
     * Load fragment với animation chuyển cảnh
     */
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

    /**
     * Lưu trạng thái màn hình hiện tại
     */
    private void saveCurrentScreen(int screenId) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(CURRENT_SCREEN_KEY, screenId).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving screen state: ", e);
        }
    }
    private boolean isCurrentFragment(String fragmentClassName) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        return currentFragment != null &&
                currentFragment.getClass().getSimpleName().equals(fragmentClassName);
    }
}