package com.example.crabquizz.Scripts.Controller;

import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

    /**
     * Phương thức chuyển đổi giữa các fragment với animation custom
     * @param fragmentManager Quản lý các fragment
     * @param fragment Màn hình fragment mới
     * @param containerId ID của container chứa fragment
     */
    public static void replaceFragmentWithAnimation(FragmentManager fragmentManager, Fragment fragment, int containerId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Áp dụng custom animation
        transaction.setCustomAnimations(
                R.anim.slide_in_right,  // Animation khi thêm fragment
                R.anim.slide_out_left,  // Animation khi xóa fragment
                R.anim.slide_in_left,   // Animation khi quay lại fragment
                R.anim.slide_out_right  // Animation khi xóa fragment cũ
        );

        // Thay thế fragment trong container
        transaction.replace(containerId, fragment);

        // Thêm fragment vào back stack để có thể quay lại
        transaction.addToBackStack(null);

        // Commit giao dịch
        transaction.commit();
    }
}
