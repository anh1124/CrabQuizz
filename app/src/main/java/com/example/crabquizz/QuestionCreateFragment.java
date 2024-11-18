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
import com.example.crabquizz.Scripts.Controller.TransitionFragemt;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class QuestionCreateFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference questionsRef;

    private TextInputEditText questionInput;
    private TextInputEditText optionAInput;
    private TextInputEditText optionBInput;
    private TextInputEditText optionCInput;
    private TextInputEditText optionDInput;
    private RadioGroup correctAnswerGroup;
    private ImageButton addQuestionButton;
    private ImageButton deleteQuestionButton;
    private TextView questionNumber;
    private Button finishButton;

    private QuestionPack currentQuestionPack;
    private int currentQuestionIndex = 1;
    private ArrayList<Question> questions = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        questionsRef = database.getReference("questions");

        if (getArguments() != null) {
            currentQuestionPack = getArguments().getParcelable("question_pack");
            if (currentQuestionPack != null) {
                questions = new ArrayList<>(currentQuestionPack.getQuestions() != null ?
                        currentQuestionPack.getQuestions() : new ArrayList<>());
                currentQuestionIndex = questions.size() + 1;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_create, container, false);
        initializeViews(view);
        setupClickListeners();
        TransitionFragemt.initializeMenuNavigation(requireContext(), getParentFragmentManager(), view);
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
        loadCurrentQuestion();
    }

    private void setupClickListeners() {
        addQuestionButton.setOnClickListener(v -> addNewQuestion());
        deleteQuestionButton.setOnClickListener(v -> deleteQuestion());
        finishButton.setOnClickListener(v -> finishQuestionPack());
    }

    private void loadCurrentQuestion() {
        if (!questions.isEmpty() && currentQuestionIndex <= questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex - 1);
            questionInput.setText(currentQuestion.getQuestion());
            optionAInput.setText(currentQuestion.getAnswer1());
            optionBInput.setText(currentQuestion.getAnswer2());
            optionCInput.setText(currentQuestion.getAnswer3());
            optionDInput.setText(currentQuestion.getAnswer4());
            setCorrectAnswerRadioButton(currentQuestion.getCorrectAnswer());
        }
    }

    private void addNewQuestion() {
        if (!validateInputs()) {
            showToast("Vui lòng điền đầy đủ thông tin câu hỏi");
            return;
        }

        Question question = createQuestionFromInputs();

        if (currentQuestionIndex <= questions.size()) {
            // Cập nhật câu hỏi hiện tại
            questions.set(currentQuestionIndex - 1, question);
        } else {
            // Thêm câu hỏi mới
            questions.add(question);
        }

        // Cập nhật QuestionPack và lưu lên Firebase
        updateQuestionPackAndSave(() -> {
            showToast("Đã lưu câu hỏi thành công");
            clearForm();
            currentQuestionIndex++;
            updateQuestionNumber();
        });
    }

    private void deleteQuestion() {
        if (currentQuestionIndex > 1 && !questions.isEmpty()) {
            questions.remove(questions.size() - 1);
            updateQuestionPackAndSave(() -> {
                showToast("Đã xóa câu hỏi thành công");
                currentQuestionIndex--;
                updateQuestionNumber();
                loadCurrentQuestion();
            });
        }
    }

    private void updateQuestionPackAndSave(Runnable onSuccess) {
        if (currentQuestionPack != null) {
            currentQuestionPack.setQuestions(questions);
            questionsRef.child(String.valueOf(currentQuestionPack.getId()))
                    .setValue(currentQuestionPack)
                    .addOnSuccessListener(aVoid -> {
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    })
                    .addOnFailureListener(e ->
                            showToast("Lỗi khi lưu dữ liệu: " + e.getMessage())
                    );
        }
    }

    private void finishQuestionPack() {
        if (questions.isEmpty()) {
            showToast("Vui lòng thêm ít nhất một câu hỏi");
            return;
        }

        // Lưu lần cuối và chuyển về màn hình chính
        updateQuestionPackAndSave(() -> {
            showToast("Đã hoàn thành bộ câu hỏi");
            // Chuyển về màn hình chính hoặc danh sách bộ câu hỏi
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private Question createQuestionFromInputs() {
        String questionText = questionInput.getText().toString().trim();
        String answer1 = optionAInput.getText().toString().trim();
        String answer2 = optionBInput.getText().toString().trim();
        String answer3 = optionCInput.getText().toString().trim();
        String answer4 = optionDInput.getText().toString().trim();
        int correctAnswer = getCorrectAnswerNumber(correctAnswerGroup.getCheckedRadioButtonId());

        return new Question(
                Math.abs(String.valueOf(System.currentTimeMillis()).hashCode()),
                questionText,
                answer1,
                answer2,
                answer3,
                answer4,
                correctAnswer
        );
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

    private void setCorrectAnswerRadioButton(int correctAnswer) {
        int radioButtonId;
        switch (correctAnswer) {
            case 1:
                radioButtonId = R.id.optionARadio;
                break;
            case 2:
                radioButtonId = R.id.optionBRadio;
                break;
            case 3:
                radioButtonId = R.id.optionCRadio;
                break;
            case 4:
                radioButtonId = R.id.optionDRadio;
                break;
            default:
                radioButtonId = R.id.optionARadio;
        }
        correctAnswerGroup.check(radioButtonId);
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