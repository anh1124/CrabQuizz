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
import com.example.crabquizz.R;
import com.example.crabquizz.StudentQuestionViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller quản lý điều hướng menu bottom navigation
 * Xử lý chuyển đổi màn hình và hiển thị menu dựa trên vai trò người dùng (giáo viên/học sinh),
 * đồng thời điều chỉnh hiệu ứng điều hướng dựa trên hướng chuyển đổi.
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

    // Map để lưu vị trí của từng tab trong bottom navigation
    private final Map<Integer, Integer> studentTabPositions = new HashMap<>();
    private final Map<Integer, Integer> teacherTabPositions = new HashMap<>();

    // Biến lưu trạng thái màn hình trước
    private int lastSelectedScreenId = -1;

    /**
     * Khởi tạo controller với context và fragment manager
     */
    public MenuNavigationClickController(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.sessionManager = SessionManager.getInstance(context);

        // Khởi tạo vị trí cho các tab của học sinh
        studentTabPositions.put(R.id.home, 0);
        studentTabPositions.put(R.id.question, 1);
        studentTabPositions.put(R.id.studentclass, 2);
        studentTabPositions.put(R.id.profile, 3);

        // Khởi tạo vị trí cho các tab của giáo viên
        teacherTabPositions.put(R.id.home, 0);
        teacherTabPositions.put(R.id.question, 1);
        teacherTabPositions.put(R.id.teacherclass, 2);
        teacherTabPositions.put(R.id.profile, 3);
    }

    /**
     * Khởi tạo và thiết lập navigation views cho cả học sinh và giáo viên
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

    /**
     * Xác định hướng chuyển đổi dựa trên vị trí của tab
     */
    private boolean determineNavigationDirection(int newItemId) {
        String userRole = sessionManager.getUserSession().getUser().getRole();
        Map<Integer, Integer> currentTabPositions = "teacher".equals(userRole)
                ? teacherTabPositions
                : studentTabPositions;

        int currentPosition = currentTabPositions.getOrDefault(lastSelectedScreenId, 0);
        int newPosition = currentTabPositions.getOrDefault(newItemId, 0);

        return newPosition > currentPosition;
    }

    /**
     * Xử lý chuyển đổi fragment, đồng thời lưu trạng thái màn hình và áp dụng hiệu ứng animation
     */
    private boolean handleNavigation(Fragment fragment, int itemId) {
        if (fragment != null) {
            boolean isForward = determineNavigationDirection(itemId);
            loadFragment(fragment, isForward);
            saveCurrentScreen(itemId);
            lastSelectedScreenId = itemId;
            return true;
        }
        return false;
    }

    /**
     * Load fragment với hiệu ứng chuyển cảnh dựa trên hướng điều hướng
     */
    private void loadFragment(Fragment fragment, boolean isForward) {
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (isForward) {
                transaction.setCustomAnimations(
                        R.anim.slide_in_right, // Hiệu ứng vào khi chuyển tiếp
                        R.anim.slide_out_left  // Hiệu ứng ra khi chuyển tiếp
                );
            } else {
                transaction.setCustomAnimations(
                        R.anim.slide_in_left,  // Hiệu ứng vào khi quay lại
                        R.anim.slide_out_right // Hiệu ứng ra khi quay lại
                );
            }
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: ", e);
        }
    }

    /**
     * Khôi phục màn hình cuối cùng được chọn (mặc định là Home)
     */
    private void restoreLastScreen() {
        int homeScreenId = R.id.home;
        lastSelectedScreenId = homeScreenId; // Lưu màn hình đầu tiên
        String userRole = sessionManager.getUserSession().getUser().getRole();

        if ("teacher".equals(userRole)) {
            teacherNavigation.setSelectedItemId(homeScreenId);
        } else {
            studentNavigation.setSelectedItemId(homeScreenId);
        }

        loadFragment(new HomeFragment(), true); // Forward mặc định cho lần đầu
    }

    /**
     * Kiểm tra xem fragment hiện tại có trùng với tên fragment mới hay không
     */
    private boolean isCurrentFragment(String fragmentClassName) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        return currentFragment != null &&
                currentFragment.getClass().getSimpleName().equals(fragmentClassName);
    }

    /**
     * Lấy fragment tương ứng với item được chọn trong menu học sinh
     */
    private Fragment getFragmentForStudentNavigation(int itemId) {
        switch (itemId) {
            case R.id.home:
                return new HomeFragment();
            case R.id.question:
                return new StudentQuestionViewFragment();
            case R.id.studentclass:
                return new ScreenStudentClassFragment();
            case R.id.profile:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    /**
     * Lấy fragment tương ứng với item được chọn trong menu giáo viên
     */
    private Fragment getFragmentForTeacherNavigation(int itemId) {
        switch (itemId) {
            case R.id.home:
                return new HomeFragment();
            case R.id.question:
                return new QuestionFragment();
            case R.id.teacherclass:
                return new ScreenTeacherClassFragment();
            case R.id.profile:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    /**
     * Lấy tên fragment dựa trên ID của item
     */
    private String getFragmentClassName(int itemId) {
        switch (itemId) {
            case R.id.home:
                return "HomeFragment";
            case R.id.teacherclass:
                return "ScreenTeacherClassFragment";
            case R.id.studentclass:
                return "ScreenStudentClassFragment";
            case R.id.question:
                return "QuestionFragment";
            case R.id.profile:
                return "ProfileFragment";
            default:
                return "";
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
}