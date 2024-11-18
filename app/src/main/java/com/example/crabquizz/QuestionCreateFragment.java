package com.example.crabquizz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.crabquizz.Scripts.Controller.TransitionFragemt;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

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
    private DbContext dbContext;

    // QuestionPack details
    private String packId;
    private String teacherId;
    private String title;
    private String description;
    private String topic;

    // Questions management
    private int currentQuestionIndex = 1;
    private ArrayList<Question> questions = new ArrayList<>();
    private static final String QUESTION_PACKS_COLLECTION = "questionpacks";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        dbContext = DbContext.getInstance();

        // Extract QuestionPack details from arguments
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
        setupClickListeners();
        TransitionFragemt.initializeMenuNavigation(requireContext(), getParentFragmentManager(), view);
        return view;
    }

    private void initializeViews(View view) {
        // Find and initialize all view components
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

        // Update UI
        updateQuestionNumber();
    }

    private void setupClickListeners() {
        addQuestionButton.setOnClickListener(v -> addNewQuestionForm());
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

    private void addNewQuestionForm() {

    }

    private void deleteQuestion() {
        clearForm();
    }

    private void saveQuestionPack(Runnable onSuccess) {
        // Convert questions to JSON
        String questionJson = convertQuestionsToJson();

        // Tạo QuestionPack
        QuestionPack questionPack = new QuestionPack(
                packId,
                teacherId,
                title,
                description,
                topic,
                questionJson
        );

        // Lưu QuestionPack vào Firestore qua DbContext
        DbContext dbContext = DbContext.getInstance();
        dbContext.add(QUESTION_PACKS_COLLECTION, questionPack)
                .addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Lỗi khi lưu dữ liệu: " + e.getMessage());
                });
    }


    private String convertQuestionsToJson() {
        // Convert questions list to JSON string
        // You'll need to add a JSON library like Gson to your project
        Gson gson = new Gson();
        return gson.toJson(questions);
    }

    private void finishQuestionPack() {
        // Validate that the current form is complete before adding to list
        if (validateInputs()) {
            Question currentQuestion = createQuestionFromInputs();
            questions.add(currentQuestion);
        }

        if (questions.isEmpty()) {
            showToast("Vui lòng thêm ít nhất một câu hỏi");
            return;
        }

        // Save final version and return to main screen
        saveQuestionPack(() -> {
            showToast("Đã hoàn thành bộ câu hỏi");
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