package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crabquizz.Scripts.Adapter.QuestionAdapter;
import com.example.crabquizz.Scripts.Dialog.QuestionDialog;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.UUID;

public class QuestionCreateFragment extends Fragment implements QuestionAdapter.QuestionInteractionListener {
    private DbContext dbContext;
    private String packId, teacherId, title, description, topic;
    private ArrayList<Question> questions = new ArrayList<>();
    private QuestionAdapter questionAdapter;

    // UI Components
    private RecyclerView questionRecyclerView;
    private FloatingActionButton addQuestionButton;
    private Button finishButton;
    private ProgressBar loadingProgress;
    private View emptyStateLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbContext = DbContext.getInstance();
        packId = UUID.randomUUID().toString();

        if (getArguments() != null) {
            //packId = getArguments().getString("id");
            teacherId = getArguments().getString("teacherId");
            title = getArguments().getString("title");
            description = getArguments().getString("description");
            topic = getArguments().getString("topic");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_create, container, false);
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        updateEmptyState();
        return view;
    }

    private void initializeViews(View view) {
        questionRecyclerView = view.findViewById(R.id.questionsRecyclerView);
        addQuestionButton = view.findViewById(R.id.AddQuestionButton);
        finishButton = view.findViewById(R.id.finishButton);
        loadingProgress = view.findViewById(R.id.loadingProgress);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
    }

    private void setupRecyclerView() {
        questionAdapter = new QuestionAdapter(questions, this);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionRecyclerView.setAdapter(questionAdapter);
    }

    private void setupClickListeners() {
        addQuestionButton.setOnClickListener(v -> showAddQuestionDialog());
        finishButton.setOnClickListener(v -> finishQuestionPack());
    }

    private void showAddQuestionDialog() {
        // Tạo một instance mới của QuestionDialog
        QuestionDialog dialog = new QuestionDialog(requireContext(), null, question -> {
            questions.add(question);
            questionAdapter.notifyItemInserted(questions.size() - 1);
            updateEmptyState();
        });
        dialog.show();
    }

    @Override
    public void onQuestionEdit(Question question) {
        // Hiển thị dialog chỉnh sửa với dữ liệu câu hỏi hiện tại
        QuestionDialog dialog = new QuestionDialog(requireContext(), question, editedQuestion -> {
            int position = questions.indexOf(question);
            if (position != -1) {
                questions.set(position, editedQuestion);
                questionAdapter.notifyItemChanged(position);
            }
        });
        dialog.show();
    }

    @Override
    public void onQuestionDelete(Question question) {
        int position = questions.indexOf(question);
        if (position != -1) {
            questions.remove(position);
            questionAdapter.notifyItemRemoved(position);
            updateEmptyState();
            showToast("Câu hỏi đã được xóa!");
        }
    }

    private void updateEmptyState() {
        if (questions.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            questionRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            questionRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void finishQuestionPack() {
        if (questions.isEmpty()) {
            showToast("Vui lòng thêm ít nhất một câu hỏi");
            return;
        }

        loadingProgress.setVisibility(View.VISIBLE);
        saveQuestionPack(() -> {
            loadingProgress.setVisibility(View.GONE);
            showToast("Đã hoàn thành bộ câu hỏi");

            // Create a new QuestionFragment and navigate to it
//            QuestionFragment questionFragment = new QuestionFragment();
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, questionFragment)
//                    .commit();
        Intent intent = new Intent(requireActivity() , StorageQuestionPackActivity.class);
        startActivity(intent);
        });
    }

    private void saveQuestionPack(Runnable onSuccess) {
        String questionJson = new Gson().toJson(questions);
        QuestionPack questionPack = new QuestionPack(packId, teacherId, title, description, topic, questionJson);

        dbContext.add("questionpacks", questionPack)
                .addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(e -> {
                    loadingProgress.setVisibility(View.GONE);
                    showToast("Lỗi khi lưu dữ liệu: " + e.getMessage());
                });
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}