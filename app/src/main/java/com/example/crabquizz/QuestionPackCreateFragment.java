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
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class QuestionPackCreateFragment extends Fragment {
    private Button createPackButton;
    private NavigationController navigationController;
    private TextInputEditText questionPackTitleInput;
    private TextInputEditText questionPackDescriptionInput;
    private TextInputEditText questionPackTopicInput;
    private DbContext dbContext;
    private static final String TAG = "QuestionPackCreate";
    private static final String QUESTION_PACKS_COLLECTION = "questionpacks";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbContext = DbContext.getInstance();
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
        // Validate input
        String title = questionPackTitleInput.getText().toString().trim();
        String description = questionPackDescriptionInput.getText().toString().trim();
        String topic = questionPackTopicInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || topic.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin bộ câu hỏi");
            return;
        }

        // Check user login
        String teacherId = SessionManager.getInstance(getContext()).getUserSession().getUser().getUsername();
        if (teacherId == null) {
            showToast("Vui lòng đăng nhập để tạo bộ câu hỏi");
            return;
        }

        // Prepare question pack data
        Map<String, Object> questionPackData = new HashMap<>();
        questionPackData.put("teacherId", teacherId);
        questionPackData.put("title", title);
        questionPackData.put("description", description);
        questionPackData.put("topic", topic);

        // Save to Firestore using DbContext
        dbContext.addWithAutoId(QUESTION_PACKS_COLLECTION, questionPackData)
                .addOnSuccessListener(documentReference -> {
                    showToast("Tạo bộ câu hỏi thành công!");

                    // Create QuestionPack object with the new document ID
                    QuestionPack questionPack = new QuestionPack(
                            documentReference.getId(),
                            teacherId,
                            title,
                            description,
                            topic,
                            null  // questionJson will be updated later
                    );

                    navigateToQuestionCreateFragment(questionPack);
                })
                .addOnFailureListener(e -> {
                    showToast("Lỗi khi lưu: " + e.getMessage());
                    Log.e(TAG, "Failed to save question pack", e);
                });
    }

    private void navigateToQuestionCreateFragment(QuestionPack questionPack) {
        Bundle bundle = new Bundle();
        bundle.putString("packId", questionPack.getId());
        bundle.putString("teacherId", questionPack.getTeacherId());
        bundle.putString("title", questionPack.getTitle());
        bundle.putString("description", questionPack.getDescription());
        bundle.putString("topic", questionPack.getTopic());

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