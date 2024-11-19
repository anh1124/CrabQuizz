package com.example.crabquizz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.Adapter.QuestionAdapter;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;

public class QuestionCreateFragment extends Fragment {
    private DbContext dbContext;

    private TextInputEditText questionInput, optionAInput, optionBInput, optionCInput, optionDInput;
    private RadioGroup correctAnswerGroup;
    private ImageButton addQuestionButton, deleteQuestionButton;
    private TextView questionNumber;
    private Button finishButton;
    private RecyclerView questionRecyclerView;
    private QuestionAdapter questionAdapter;

    private String packId, teacherId, title, description, topic;
    private int currentQuestionIndex = 1;
    private ArrayList<Question> questions = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbContext = DbContext.getInstance();

        if (getArguments() != null) {
            packId = getArguments().getString("packId");
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
        setupRecyclerView(view);
        setupClickListeners();
        return view;
    }

    private void initializeViews(View view) {
        questionInput = view.findViewById(R.id.questionInput);
        optionAInput = view.findViewById(R.id.optionAInput);
        optionBInput = view.findViewById(R.id.optionBInput);
        optionCInput = view.findViewById(R.id.optionCInput);
        optionDInput = view.findViewById(R.id.optionDInput);
        correctAnswerGroup = view.findViewById(R.id.correctAnswerGroup);
        addQuestionButton = view.findViewById(R.id.AddQuestionButton);
        deleteQuestionButton = view.findViewById(R.id.deleteQuestionButton);
        questionNumber = view.findViewById(R.id.questionNumber);
        finishButton = view.findViewById(R.id.finishButton);

        updateQuestionNumber();
    }

    private void setupRecyclerView(View view) {
        questionRecyclerView = view.findViewById(R.id.questionsRecyclerView);
        questionAdapter = new QuestionAdapter(questions);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionRecyclerView.setAdapter(questionAdapter);
    }

    private void setupClickListeners() {
        addQuestionButton.setOnClickListener(v -> addNewQuestionForm());
        deleteQuestionButton.setOnClickListener(v -> clearForm());
        finishButton.setOnClickListener(v -> finishQuestionPack());
    }

    private void addNewQuestionForm() {
        if (validateInputs()) {
            Question currentQuestion = createQuestionFromInputs();
            questions.add(currentQuestion);
            questionAdapter.notifyItemInserted(questions.size() - 1);

            currentQuestionIndex++;
            updateQuestionNumber();
            clearForm();
            showToast("Câu hỏi đã được thêm!");
        } else {
            showToast("Vui lòng điền đầy đủ thông tin câu hỏi.");
        }
    }

    private void finishQuestionPack() {
        if (validateInputs()) {
            Question currentQuestion = createQuestionFromInputs();
            questions.add(currentQuestion);
        }

        if (questions.isEmpty()) {
            showToast("Vui lòng thêm ít nhất một câu hỏi");
            return;
        }

        saveQuestionPack(() -> {
            showToast("Đã hoàn thành bộ câu hỏi");
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void saveQuestionPack(Runnable onSuccess) {
        String questionJson = convertQuestionsToJson();
        QuestionPack questionPack = new QuestionPack(packId, teacherId, title, description, topic, questionJson);

        dbContext.add("questionpacks", questionPack)
                .addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(e -> showToast("Lỗi khi lưu dữ liệu: " + e.getMessage()));
    }

    private String convertQuestionsToJson() {
        Gson gson = new Gson();
        return gson.toJson(questions);
    }

    private Question createQuestionFromInputs() {
        String questionText = questionInput.getText().toString().trim();
        String answer1 = optionAInput.getText().toString().trim();
        String answer2 = optionBInput.getText().toString().trim();
        String answer3 = optionCInput.getText().toString().trim();
        String answer4 = optionDInput.getText().toString().trim();
        int correctAnswer = getCorrectAnswerNumber(correctAnswerGroup.getCheckedRadioButtonId());

        return new Question(Math.abs(String.valueOf(System.currentTimeMillis()).hashCode()), questionText, answer1, answer2, answer3, answer4, correctAnswer);
    }

    private boolean validateInputs() {
        return !questionInput.getText().toString().trim().isEmpty() &&
                !optionAInput.getText().toString().trim().isEmpty() &&
                !optionBInput.getText().toString().trim().isEmpty() &&
                !optionCInput.getText().toString().trim().isEmpty() &&
                !optionDInput.getText().toString().trim().isEmpty() &&
                correctAnswerGroup.getCheckedRadioButtonId() != -1;
    }

    private void clearForm() {
        questionInput.setText("");
        optionAInput.setText("");
        optionBInput.setText("");
        optionCInput.setText("");
        optionDInput.setText("");
        correctAnswerGroup.clearCheck();
    }

    private void updateQuestionNumber() {
        questionNumber.setText(String.format("Câu hỏi số %d", currentQuestionIndex));
        deleteQuestionButton.setEnabled(currentQuestionIndex > 1);
    }

    private int getCorrectAnswerNumber(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.optionARadio:
                return 1;
            case R.id.optionBRadio:
                return 2;
            case R.id.optionCRadio:
                return 3;
            case R.id.optionDRadio:
                return 4;
            default:
                return 1;
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
