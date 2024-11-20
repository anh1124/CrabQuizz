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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.StudentClassController;

/**
 * Fragment hiển thị màn hình chính của ứng dụng
 * Cho phép người dùng đăng nhập/đăng ký và tham gia quiz
 */
public class HomeFragment extends Fragment {
    // Constants
    private static final String TAG = "HomeFragment";

    // UI Components
    private TextView tvGreeting;          // Hiển thị lời chào
    private Button btnLoginSignup;        // Nút đăng nhập/đăng ký
    private Button btnJoinClass;          // Nút tham gia class
    private EditText editClassCode;         // Ô nhập mã class
    private ImageView imageView;          // Ảnh logo
    private View rootView;                // View gốc của fragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            initView();
            setGreeting();
            showLoginSignupButton();
            setupImage();

            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: ", e);
            showToast("Đã xảy ra lỗi khi tải giao diện");
            return new View(requireContext());
        }
    }




    /**
     * Hiển thị hoặc ẩn nút đăng nhập/đăng ký dựa trên trạng thái người dùng
     */
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

    /**
     * Cài đặt ảnh logo
     */
    private void setupImage() {
        if (imageView != null) {
            try {
                imageView.setImageResource(R.drawable.university_student_cap_mortar_board_and_diploma);
            } catch (Exception e) {
                Log.e(TAG, "Error setting image: ", e);
            }
        }
    }

    /**
     * Điều chỉnh trạng thái hiển thị của nút đăng nhập/đăng ký
     */
    private void setLoginSignupButtonVisibility(boolean isVisible) {
        if (btnLoginSignup != null) {
            btnLoginSignup.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Khởi tạo các view và thiết lập listener
     */
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


        editClassCode = rootView.findViewById(R.id.edtClassCode);
        btnJoinClass = rootView.findViewById(R.id.btnJoinClass);

        //join class btn
        btnJoinClass.setOnClickListener(v -> {
            if(IsLogin())
            {
                String classCode = editClassCode.getText().toString().trim();
                if (!classCode.isEmpty()) {
                    showToast("Đang kiểm tra mã lớp...");

                    StudentClassController controller = new StudentClassController();
                    controller.getClassById(classCode)
                            .addOnSuccessListener(studentClass -> {
                                Log.e("getClassById", "Class ID: " + studentClass.getId() + ", Class Name: " + studentClass.getName() + ", Student Count: " + studentClass.getStudentCount());
                                if (studentClass != null) {
                                    Intent intent = new Intent(requireActivity(), JoinAndInfoClass.class);
                                    // Gửi thông tin lớp qua Intent
                                    intent.putExtra("classId", studentClass.getId());
                                    intent.putExtra("className", studentClass.getName());
                                    intent.putExtra("studentCount", studentClass.getStudentCount());
                                    startActivity(intent);
                                } else {
                                    showToast("Không tìm thấy lớp với mã này.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error fetching class: ", e);
                                showToast("Đã xảy ra lỗi khi kiểm tra mã lớp.");
                            });
                } else {
                    showToast("Vui lòng nhập mã tham gia.");
                }
            }
            else
            {
                showToast("Vui lòng đăng nhập trước khi tham gia lớp.");
            }

        });
    }
    private boolean IsLogin()
    {
        if(SessionManager.getInstance(requireContext()).isLogin()) {
            return true;
        }
        return false;
    }

    /**
     * Thiết lập lời chào dựa trên thời gian và thông tin người dùng
     */
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

    /**
     * Xác định thời điểm trong ngày (sáng/chiều/tối)
     * @return String mô tả thời điểm trong ngày
     */
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

    /**
     * Hiển thị thông báo toast
     * @param message Nội dung thông báo
     */
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}