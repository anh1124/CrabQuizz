package com.example.crabquizz.Scripts.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.Question;
import com.google.android.material.textfield.TextInputEditText;

public class QuestionDialog extends Dialog {
    private final Question existingQuestion;
    private final OnQuestionSavedListener listener;

    // UI Components
    private TextInputEditText questionInput, optionAInput, optionBInput, optionCInput, optionDInput;
    private RadioGroup correctAnswerGroup;
    private Button saveButton, cancelButton;

    public interface OnQuestionSavedListener {
        void onQuestionSaved(Question question);
    }

    public QuestionDialog(Context context, Question existingQuestion, OnQuestionSavedListener listener) {
        super(context);
        this.existingQuestion = existingQuestion;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_question); // Sử dụng layout mới

        initializeViews();
        if (existingQuestion != null) {
            fillFormWithQuestion(existingQuestion);
        }
        setupClickListeners();
    }

    private void initializeViews() {
        questionInput = findViewById(R.id.questionInput);
        optionAInput = findViewById(R.id.optionAInput);
        optionBInput = findViewById(R.id.optionBInput);
        optionCInput = findViewById(R.id.optionCInput);
        optionDInput = findViewById(R.id.optionDInput);
        correctAnswerGroup = findViewById(R.id.correctAnswerGroup);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void fillFormWithQuestion(Question question) {
        questionInput.setText(question.getQuestion());
        optionAInput.setText(question.getAnswer1());
        optionBInput.setText(question.getAnswer2());
        optionCInput.setText(question.getAnswer3());
        optionDInput.setText(question.getAnswer4());

        int radioButtonId;
        switch (question.getCorrectAnswer()) {
            case 1: radioButtonId = R.id.optionARadio; break;
            case 2: radioButtonId = R.id.optionBRadio; break;
            case 3: radioButtonId = R.id.optionCRadio; break;
            case 4: radioButtonId = R.id.optionDRadio; break;
            default: radioButtonId = R.id.optionARadio;
        }
        correctAnswerGroup.check(radioButtonId);
    }

    private void setupClickListeners() {
        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveQuestion();
            } else {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin câu hỏi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        return !questionInput.getText().toString().trim().isEmpty() &&
                !optionAInput.getText().toString().trim().isEmpty() &&
                !optionBInput.getText().toString().trim().isEmpty() &&
                !optionCInput.getText().toString().trim().isEmpty() &&
                !optionDInput.getText().toString().trim().isEmpty() &&
                correctAnswerGroup.getCheckedRadioButtonId() != -1;
    }

    private void saveQuestion() {
        Question question = createQuestionFromInputs();
        listener.onQuestionSaved(question);
        dismiss();
    }

    private Question createQuestionFromInputs() {
        String questionText = questionInput.getText().toString().trim();
        String answer1 = optionAInput.getText().toString().trim();
        String answer2 = optionBInput.getText().toString().trim();
        String answer3 = optionCInput.getText().toString().trim();
        String answer4 = optionDInput.getText().toString().trim();
        int correctAnswer = getCorrectAnswerNumber();

        int id = existingQuestion != null ? existingQuestion.getId() : generateUniqueId();

        return new Question(id, questionText, answer1, answer2, answer3, answer4, correctAnswer);
    }

    private int generateUniqueId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    private int getCorrectAnswerNumber() {
        int id = correctAnswerGroup.getCheckedRadioButtonId();
        if (id == R.id.optionARadio) return 1;
        if (id == R.id.optionBRadio) return 2;
        if (id == R.id.optionCRadio) return 3;
        if (id == R.id.optionDRadio) return 4;
        return 1;
    }
}