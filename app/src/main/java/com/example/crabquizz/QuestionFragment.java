package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.NavigationController;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.TransitionFragemt;

public class QuestionFragment extends Fragment {
    private Button button2;
    private View rootView;
    private NavigationController navigationController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        rootView = inflater.inflate(R.layout.fragment_question, container, false);
        navigationController = new NavigationController(requireActivity());

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Set up navigation
        TransitionFragemt.initializeMenuNavigation(requireContext(), getParentFragmentManager(), rootView);

        return rootView;
    }

    private void initializeViews() {
        button2 = rootView.findViewById(R.id.button2);
    }

    private void setupClickListeners() {
        button2.setOnClickListener(view -> {
            // Tạo instance mới của QuestionCreateFragment
            QuestionPackCreateFragment fragment = new QuestionPackCreateFragment();
            String role = SessionManager.getInstance(requireContext())
                    .getUserSession()
                    .getUser()
                    .getRole();

            switch (role) {
                case "teacher":
                    // Sử dụng NavigationController để điều hướng
                    navigationController.navigateTo(fragment);
                    break;

                case "student":
                    Toast.makeText(requireContext(),
                            "Chỉ giáo viên mới thể tạo câu hỏi",
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(requireContext(),
                            "Đăng nhập để tiếp tục",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}