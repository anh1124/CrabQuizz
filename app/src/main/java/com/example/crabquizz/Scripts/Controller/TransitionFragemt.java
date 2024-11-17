package com.example.crabquizz.Scripts.Controller;

import android.content.Context;
import android.view.View;
import androidx.fragment.app.FragmentManager;

import com.example.crabquizz.HomeFragment;
import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;

public class TransitionFragemt {
    // Phương thức này sẽ khởi tạo và quản lý việc chuyển đổi giữa các fragments
    public static void initializeMenuNavigation(Context context, FragmentManager fragmentManager, View rootView) {
        // Khởi tạo controller MenuNavigationClickController
        MenuNavigationClickController controller = new MenuNavigationClickController(
                context,
                fragmentManager
        );

        // Lấy các view của thanh điều hướng
        View studentNav = rootView.findViewById(R.id.studentBottomNavigation);
        View teacherNav = rootView.findViewById(R.id.teacherBottomNavigation);

        // Kiểm tra và khởi tạo navigation nếu các view không null
        if (studentNav != null && teacherNav != null) {
            controller.initializeNavigations(studentNav, teacherNav);
        }


    }
}

