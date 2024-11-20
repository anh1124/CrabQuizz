package com.example.crabquizz;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenTeacherClassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenTeacherClassFragment extends Fragment {
    private RecyclerView classRecyclerView;
    private ProgressBar loadingProgress;
    private View emptyStateLayout;
    private ClassTeacherAdapter classTeacherAdapter;
    private StudentClassController classController;
    private SessionManager sessionManager;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScreenTeacherClassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScreenTeacherClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScreenTeacherClassFragment newInstance(String param1, String param2) {
        ScreenTeacherClassFragment fragment = new ScreenTeacherClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_teacher_class, container, false);
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

        // Pass view to initView
        initView(view);
        initPack();
    }

    private void initPack() {
        sessionManager = SessionManager.getInstance(requireContext());
    }

    private void initView(@NonNull View view) {
        // Find the FloatingActionButton
        View addClassFab = view.findViewById(R.id.addClassFab);

        // Set a click listener for the FAB
        addClassFab.setOnClickListener(v -> {
            goCreateClassAtivity();
        });

        classRecyclerView = view.findViewById(R.id.classRecyclerView);
        loadingProgress = view.findViewById(R.id.loadingProgress);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);

        // Set up RecyclerView
        classRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classTeacherAdapter = new ClassTeacherAdapter(new ArrayList<>());
        classRecyclerView.setAdapter(classTeacherAdapter);

        // Initialize controller
        classController = new StudentClassController();

        // Fetch teacher's classes
        int teacherId = getTeacherId();
        fetchTeacherClasses(teacherId);
    }


    private void fetchTeacherClasses(int teacherId) {
        showLoading(true);
        Log.d("fetchTeacherClasses", "Fetching classes for teacher: " + teacherId);

        classController.getTeacherClassesAsJson(teacherId)
                .addOnSuccessListener(jsonResult -> {
                    Log.d("fetchTeacherClasses", "Received JSON result: " + jsonResult);
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
                                classTeacherAdapter.updateData(classes);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("fetchTeacherClasses", "Error parsing JSON", e);
                            showEmptyState(true);
                        }
                    } else {
                        showEmptyState(true);
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showEmptyState(true);
                    Log.e("fetchTeacherClasses", "Failed to fetch classes", e);
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

    private int getTeacherId() {

        // Replace with your logic to fetch the logged-in teacher's ID
        int teacherId = sessionManager.getInstance(requireContext()).getUserSession().getUser().getId();
        Log.e("ScreenTeacherClassFragment", String.valueOf(teacherId));
        return teacherId;
    }


    //hàm dưới chịu trách nhiện nhận code refrest trang
    private static final int REQUEST_CODE_CREATE_CLASS = 1;

    private void goCreateClassAtivity() {
        // Mở Activity và yêu cầu kết quả
        Intent intent = new Intent(requireContext(), CreateClass.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_CLASS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_CLASS && resultCode == requireActivity().RESULT_OK) {
            // Khi Activity trả về kết quả, làm mới danh sách lớp
            int teacherId = getTeacherId();
            fetchTeacherClasses(teacherId);
        }
    }
//end
}