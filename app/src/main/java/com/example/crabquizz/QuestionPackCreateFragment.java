package com.example.crabquizz;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.crabquizz.Scripts.Controller.NavigationController;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.TransitionFragemt;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionPackCreateFragment extends Fragment {
    private Button createPackButton;
    private NavigationController navigationController;
    private TextInputEditText questionPackTitleInput;
    private TextInputEditText questionPackDescriptionInput;
    private TextInputEditText questionPackTopicInput;
    private FirebaseDatabase database;
    private DatabaseReference questionsRef;
    private static final String TAG = "QuestionPackCreate";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        questionsRef = database.getReference("questionpacks");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_pack_create, container, false);
        navigationController = new NavigationController(requireActivity());
        initView(view);
        setupClickListener();
        return view;
    }

    private void initView(View view) {
        createPackButton = view.findViewById(R.id.createPackButton);
        questionPackTitleInput = view.findViewById(R.id.questionPackTitleInput);
        questionPackDescriptionInput = view.findViewById(R.id.questionPackDescriptionInput);
        questionPackTopicInput = view.findViewById(R.id.questionPackTopicInput);
    }

    private void setupClickListener() {
        createPackButton.setOnClickListener(v -> createAndNavigate());
    }

    private void createAndNavigate() {
        // Lấy và kiểm tra dữ liệu đầu vào
        String title = questionPackTitleInput.getText().toString().trim();
        String description = questionPackDescriptionInput.getText().toString().trim();
        String topic = questionPackTopicInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || topic.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin bộ câu hỏi");
            return;
        }

        // Kiểm tra người dùng đã đăng nhập
        String teacherId = SessionManager.getInstance(getContext()).getUserSession().getUser().getUsername();
        if (teacherId == null) {
            showToast("Vui lòng đăng nhập để tạo bộ câu hỏi");
            return;
        }

        // Tạo ID cho bộ câu hỏi mới
        String packId = questionsRef.push().getKey();
        if (packId == null) {
            showToast("Không thể tạo ID cho bộ câu hỏi. Vui lòng thử lại.");
            return;
        }

        // Log thông tin để debug
        Log.d(TAG, "Generated packId: " + packId);
        Log.d(TAG, "Teacher ID: " + teacherId);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Description: " + description);
        Log.d(TAG, "Topic: " + topic);

        // Tạo đối tượng QuestionPack
        QuestionPack questionPack = new QuestionPack(
                packId,
                teacherId,
                title,
                description,
                topic
        );

        // Lưu dữ liệu lên Firebase
        questionsRef.child(packId).setValue(questionPack)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Data saved successfully to Firebase");
                        showToast("Tạo bộ câu hỏi thành công!");
                        navigateToQuestionCreateFragment(questionPack);
                    } else {
                        Log.e(TAG, "Failed to save data", task.getException());
                        showToast("Đã xảy ra lỗi khi tạo bộ câu hỏi. Vui lòng thử lại.");
                    }
                });
    }

    private void navigateToQuestionCreateFragment(QuestionPack questionPack) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("question_pack", questionPack);

        QuestionCreateFragment createFragment = new QuestionCreateFragment();
        createFragment.setArguments(bundle);
        navigationController.navigateTo(createFragment);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}