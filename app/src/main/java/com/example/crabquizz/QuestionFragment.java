package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;

public class QuestionFragment extends Fragment {
    private Button button2;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        rootView = inflater.inflate(R.layout.fragment_question, container, false);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Set up navigation
        setupNavigation();

        return rootView;
    }

    private void initializeViews() {
        button2 = rootView.findViewById(R.id.button2);
    }

    private void setupClickListeners() {
        button2.setOnClickListener(view -> {
            // Tạo instance mới của QuestionCreateFragment
            QuestionCreateFragment createFragment = new QuestionCreateFragment();

            // Thực hiện transaction
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, createFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupNavigation() {
        // Initialize MenuNavigationClickController with the context and FragmentManager
        MenuNavigationClickController controller = new MenuNavigationClickController(
                requireContext(),
                getParentFragmentManager() // or getChildFragmentManager() based on your use case
        );

        // Find both student and teacher navigation views in the layout
        View studentNav = rootView.findViewById(R.id.studentBottomNavigation);
        View teacherNav = rootView.findViewById(R.id.teacherBottomNavigation);

        // Initialize navigation if the views are available
        if (studentNav != null || teacherNav != null) {
            controller.initializeNavigations(
                    studentNav,  // For student or default screen
                    teacherNav   // For teacher if available
            );
        }
    }

}