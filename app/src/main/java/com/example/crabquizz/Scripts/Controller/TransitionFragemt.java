package com.example.crabquizz.Scripts.Controller;

import android.content.Context;
import android.view.View;
import androidx.fragment.app.FragmentManager;
import com.example.crabquizz.R;

/**
 * Lớp quản lý việc chuyển đổi giữa các fragment và khởi tạo thanh điều hướng
 */
public class TransitionFragemt {
    /**
     * Khởi tạo thanh điều hướng và thiết lập các sự kiện chuyển fragment
     * @param context Context của ứng dụng
     * @param fragmentManager Quản lý các fragment
     * @param rootView View gốc chứa thanh điều hướng
     */
    public static void initializeMenuNavigation(Context context, FragmentManager fragmentManager, View rootView) {
        // Tạo controller xử lý sự kiện điều hướng
        MenuNavigationClickController controller = new MenuNavigationClickController(context, fragmentManager);

        // Lấy view của hai thanh điều hướng từ layout
        View studentNav = rootView.findViewById(R.id.studentBottomNavigation);
        View teacherNav = rootView.findViewById(R.id.teacherBottomNavigation);

        // Khởi tạo điều hướng nếu các view tồn tại
        if (studentNav != null && teacherNav != null) {
            controller.initializeNavigations(studentNav, teacherNav);
        }
    }
}