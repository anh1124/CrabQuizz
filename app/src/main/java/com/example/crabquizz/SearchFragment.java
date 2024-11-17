package com.example.crabquizz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;

public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up window insets for padding adjustment
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up MenuNavigationClickController
        MenuNavigationClickController controller = new MenuNavigationClickController(
                requireContext(),
                getParentFragmentManager() // or getChildFragmentManager() based on your use case
        );

        // Find both student and teacher navigation views in the layout
        View studentNav = view.findViewById(R.id.studentBottomNavigation);
        View teacherNav = view.findViewById(R.id.teacherBottomNavigation);

        // Initialize navigation if the views are available
        if (studentNav != null || teacherNav != null) {
            controller.initializeNavigations(
                    studentNav,  // For student or default screen
                    teacherNav   // For teacher if available
            );
        }
    }
}