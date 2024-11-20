package com.example.crabquizz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.crabquizz.Scripts.Adapter.ClassStudentAdapter;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.StudentClassController;
import com.example.crabquizz.Scripts.Models.StudentClass;
import com.example.crabquizz.Scripts.Adapter.ClassTeacherAdapter;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


public class ScreenStudentClassFragment extends Fragment {

        private RecyclerView classRecyclerView;
        private ProgressBar loadingProgress;
        private View emptyStateLayout;
        private ClassStudentAdapter classStudentAdapter;
        private StudentClassController classController;
        private SessionManager sessionManager;

        // Factory method parameters
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";
        private String mParam1;
        private String mParam2;

        public ScreenStudentClassFragment() {
            // Required empty public constructor
        }

        public static ScreenStudentClassFragment newInstance(String param1, String param2) {
            ScreenStudentClassFragment fragment = new ScreenStudentClassFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_screen_student_class, container, false);
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
                    getParentFragmentManager()
            );

            // Find both student and teacher navigation views in the layout
            View studentNav = view.findViewById(R.id.studentBottomNavigation);
            View teacherNav = view.findViewById(R.id.teacherBottomNavigation);

            // Initialize navigation if the views are available
            if (studentNav != null || teacherNav != null) {
                controller.initializeNavigations(
                        studentNav,
                        teacherNav
                );
            }

            initView(view);
            initPack();
        }

        private void initPack() {
            sessionManager = SessionManager.getInstance(requireContext());
        }

        private void initView(@NonNull View view) {
            classRecyclerView = view.findViewById(R.id.classRecyclerView);
            loadingProgress = view.findViewById(R.id.loadingProgress);
            emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

            // Set up RecyclerView
            classRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            classStudentAdapter = new ClassStudentAdapter(new ArrayList<>());
            classRecyclerView.setAdapter(classStudentAdapter);

            // Initialize controller
            classController = new StudentClassController();

            // Fetch student's classes
            int studentId = getStudentId();
            fetchStudentClasses(studentId);
        }

        private void fetchStudentClasses(int studentId) {
            showLoading(true);
            Log.d("fetchStudentClasses", "Fetching classes for student: " + studentId);

            classController.getClassesForStudentAsJson(studentId)
                    .addOnSuccessListener(jsonResult -> {
                        Log.d("fetchStudentClasses", "Received JSON result: " + jsonResult);
                        showLoading(false);

                        if (jsonResult != null && !jsonResult.equals("[]")) {
                            try {
                                Gson gson = new GsonBuilder()
                                        .serializeNulls()
                                        .create();
                                Type listType = new TypeToken<ArrayList<StudentClass>>(){}.getType();
                                List<StudentClass> classes = gson.fromJson(jsonResult, listType);

                                if (classes == null || classes.isEmpty()) {
                                    showEmptyState(true);
                                } else {
                                    showEmptyState(false);
                                    classStudentAdapter.updateData(classes);
                                }
                            } catch (JsonSyntaxException e) {
                                Log.e("fetchStudentClasses", "Error parsing JSON", e);
                                showEmptyState(true);
                            }
                        } else {
                            showEmptyState(true);
                        }
                    })
                    .addOnFailureListener(e -> {
                        showLoading(false);
                        showEmptyState(true);
                        Log.e("fetchStudentClasses", "Failed to fetch classes", e);
                    });
        }

        private void showLoading(boolean isLoading) {
            loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            classRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }

        private void showEmptyState(boolean isEmpty) {
            emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            classRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }

        private int getStudentId() {
            return sessionManager.getInstance(requireContext()).getUserSession().getUser().getId();
        }
    }
