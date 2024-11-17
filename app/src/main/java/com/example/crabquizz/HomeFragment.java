package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.SessionManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private TextView tvGreeting;
    private Button btnLoginSignup;
    private Button btnStartQuiz;
    private EditText edtQuizCode;
    private ImageView imageView;
    private View rootView;

    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            initView();
            setGreeting();
            showLoginSignupButton();
            setupImage();
            setupQuizControls();

            // Initialize controller and pass context
            MenuNavigationClickController controller = new MenuNavigationClickController(
                    requireContext(),
                    getParentFragmentManager()
            );

            View studentNav = rootView.findViewById(R.id.studentBottomNavigation);
            View teacherNav = rootView.findViewById(R.id.teacherBottomNavigation);

            if (studentNav != null && teacherNav != null) {
                controller.initializeNavigations(studentNav, teacherNav);
            }

            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: ", e);
            showToast("Đã xảy ra lỗi khi tải giao diện");
            return new View(requireContext());
        }
    }

    private void setupQuizControls() {
        edtQuizCode = rootView.findViewById(R.id.edtQuizCode);
        btnStartQuiz = rootView.findViewById(R.id.btnStartQuiz);

        if (btnStartQuiz != null) {
            btnStartQuiz.setOnClickListener(v -> {
                String quizCode = edtQuizCode.getText().toString().trim();
                if (!quizCode.isEmpty()) {
                    // TODO: Implement quiz start logic
                    showToast("Bắt đầu quiz với mã: " + quizCode);
                } else {
                    showToast("Vui lòng nhập mã tham gia");
                }
            });
        }
    }

    private void showLoginSignupButton() {
        try {
            SessionManager.UserTEMPSession userSession = SessionManager.getInstance(requireContext()).getUserSession();

            if (userSession != null && userSession.getUser() != null) {
                String role = userSession.getUser().getRole();
                Log.d(TAG, "Role: " + role);

                setLoginSignupButtonVisibility(!(role.equals("teacher") || role.equals("student")));
            } else {
                setLoginSignupButtonVisibility(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showLoginSignupButton: ", e);
            setLoginSignupButtonVisibility(true);
        }
    }

    private void setupImage() {
        if (imageView != null) {
            try {
                imageView.setImageResource(R.drawable.university_student_cap_mortar_board_and_diploma);
            } catch (Exception e) {
                Log.e(TAG, "Error setting image: ", e);
            }
        }
    }

    private void setLoginSignupButtonVisibility(boolean isVisible) {
        if (btnLoginSignup != null) {
            btnLoginSignup.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void initView() {
        tvGreeting = rootView.findViewById(R.id.textViewGreeting);
        btnLoginSignup = rootView.findViewById(R.id.btnLoginSignup);
        imageView = rootView.findViewById(R.id.imageView);

        if (btnLoginSignup != null) {
            btnLoginSignup.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), Register.class);
                startActivity(intent);
            });
        }
    }

    private void setGreeting() {
        if (tvGreeting != null) {
            try {
                SessionManager sessionManager = SessionManager.getInstance(requireContext());
                String timeOfDay = getTimeOfDay();
                String greeting = "Chào buổi " + timeOfDay;

                SessionManager.UserTEMPSession userTEMPSession = sessionManager.getUserSession();
                if (userTEMPSession != null && userTEMPSession.getUser() != null) {
                    greeting += " " + userTEMPSession.getUser().getFullName();
                } else {
                    greeting += " Guest";
                }
                tvGreeting.setText(greeting);
            } catch (Exception e) {
                Log.e(TAG, "Error setting greeting: ", e);
                tvGreeting.setText("Chào mừng");
            }
        }
    }

    private String getTimeOfDay() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int timeOfDay = c.get(java.util.Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "sáng";
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            return "chiều";
        } else {
            return "tối";
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
