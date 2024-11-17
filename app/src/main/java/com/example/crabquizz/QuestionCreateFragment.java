package com.example.crabquizz;

import static com.example.crabquizz.R.id.studentBottomNavigation;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.TransitionFragemt;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QuestionCreateFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference questionsRef;
    private DbContext dbContext;

    // Question Pack Views
    private TextInputEditText questionPackTitleInput;
    private TextInputEditText questionPackDescriptionInput;
    private TextInputEditText questionPackTopicInput;
    private Button createPackButton;

    // Question Views
    private TextInputEditText questionInput;
    private TextInputEditText optionAInput, optionBInput, optionCInput, optionDInput;
    private RadioGroup correctAnswerGroup;
    private ImageButton addQuestionButton, deleteQuestionButton;
    private TextView questionNumber;

    // Data
    private QuestionPack currentQuestionPack;
    private int currentQuestionIndex = 1;
    private ArrayList<Question> questions = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        questionsRef = database.getReference("questions");
        dbContext = DbContext.getInstance();
    }

    @SuppressLint("MissingInflatedId")
    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for the question creation screen
        View view = inflater.inflate(R.layout.fragment_question_create, container, false);

        // Initialize view components
        initializeViews(view);

        // Setup the initial state of the fragment
        setupInitialState();

        // Setup click listeners for interactive components
        setupClickListeners();

        TransitionFragemt.initializeMenuNavigation(requireContext(), getParentFragmentManager(), view);

        return view;
    }



    private void initializeViews(View view) {
        // Initialize Question Pack Views
        questionPackTitleInput = view.findViewById(R.id.questionPackTitleInput);
        questionPackDescriptionInput = view.findViewById(R.id.questionPackDescriptionInput);
        questionPackTopicInput = view.findViewById(R.id.questionPackTopicInput);
        createPackButton = view.findViewById(R.id.createPackButton);

        // Initialize Question Views
        questionInput = view.findViewById(R.id.questionInput);
        optionAInput = view.findViewById(R.id.optionAInput);
        optionBInput = view.findViewById(R.id.optionBInput);
        optionCInput = view.findViewById(R.id.optionCInput);
        optionDInput = view.findViewById(R.id.optionDInput);
        correctAnswerGroup = view.findViewById(R.id.correctAnswerGroup);
        //saveButton = view.findViewById(R.id.saveButton);

        addQuestionButton = view.findViewById(R.id.AddQuestionButton);
        deleteQuestionButton = view.findViewById(R.id.deleteQuestionButton);
        questionNumber = view.findViewById(R.id.questionNumber);
    }

    private void setupInitialState() {
        // Disable question creation until pack is created
        setQuestionInputsEnabled(false);
        updateQuestionNumber();
    }

    private void setupClickListeners() {
        createPackButton.setOnClickListener(v -> createQuestionPack());
        addQuestionButton.setOnClickListener(v -> addNewQuestion());
        deleteQuestionButton.setOnClickListener(v -> deleteQuestion());

    }

    private void createQuestionPack() {
        String title = questionPackTitleInput.getText().toString().trim();
        String description = questionPackDescriptionInput.getText().toString().trim();
        String topic = questionPackTopicInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || topic.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin gói câu hỏi");
            return;
        }

        if (getContext() == null) return;

        String teacherId = SessionManager.getInstance(getContext()).getUserSession().getUser().getUsername();
        if (teacherId == null) {
            showToast("Vui lòng đăng nhập để tạo gói câu hỏi");
            return;
        }

        String packId = questionsRef.push().getKey();
        currentQuestionPack = new QuestionPack(
                packId != null ? packId.hashCode() : System.currentTimeMillis(),
                teacherId,
                title,
                description,
                topic
        );

        // Disable pack inputs and enable question inputs
        setPackInputsEnabled(false);
        setQuestionInputsEnabled(true);
        showToast("Đã tạo gói câu hỏi. Hãy thêm câu hỏi vào gói.");
    }

    private void saveQuestion() {
        if (!validateInputs()) {
            return;
        }

        Question question = createQuestionFromInputs();
        if (currentQuestionIndex <= questions.size()) {
            questions.set(currentQuestionIndex - 1, question);
        } else {
            questions.add(question);
        }

        currentQuestionPack.setQuestions(questions);
        saveQuestionPackToDatabase();

        // Move to next question
        currentQuestionIndex++;
        clearForm();
        updateQuestionNumber();
    }

    private Question createQuestionFromInputs() {
        String questionText = questionInput.getText().toString().trim();
        String answer1 = optionAInput.getText().toString().trim();
        String answer2 = optionBInput.getText().toString().trim();
        String answer3 = optionCInput.getText().toString().trim();
        String answer4 = optionDInput.getText().toString().trim();
        int correctAnswer = getCorrectAnswerNumber(correctAnswerGroup.getCheckedRadioButtonId());

        return new Question(
                String.valueOf(System.currentTimeMillis()).hashCode(),
                questionText,
                answer1,
                answer2,
                answer3,
                answer4,
                correctAnswer
        );
    }

    private void saveQuestionPackToDatabase() {
        dbContext.addWithAutoId("questionPacks", currentQuestionPack)
                .addOnSuccessListener(aVoid ->
                        showToast("Đã lưu câu hỏi vào gói"))
                .addOnFailureListener(e ->
                        showToast("Lỗi khi lưu: " + e.getMessage()));
    }

    private void deleteQuestion() {
        if (questions.isEmpty() || currentQuestionIndex > questions.size()) {
            clearForm();
            return;
        }

        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa câu hỏi này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (currentQuestionIndex <= questions.size()) {
                        questions.remove(currentQuestionIndex - 1);
                        if (currentQuestionIndex > 1) currentQuestionIndex--;
                        if (!questions.isEmpty()) {
                            loadQuestion(questions.get(currentQuestionIndex - 1));
                        } else {
                            clearForm();
                        }
                        updateQuestionNumber();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addNewQuestion() {
        currentQuestionIndex = questions.size() + 1;
        clearForm();
        updateQuestionNumber();
    }

    private void loadQuestion(Question question) {
        questionInput.setText(question.getQuestion());
        optionAInput.setText(question.getAnswer1());
        optionBInput.setText(question.getAnswer2());
        optionCInput.setText(question.getAnswer3());
        optionDInput.setText(question.getAnswer4());
        setCorrectAnswerRadio(question.getCorrectAnswer());
    }

    private boolean validateInputs() {
        if (currentQuestionPack == null) {
            showToast("Vui lòng tạo gói câu hỏi trước");
            return false;
        }

        if (questionInput.getText().toString().trim().isEmpty()) {
            questionInput.setError("Câu hỏi không được để trống");
            return false;
        }

        if (optionAInput.getText().toString().trim().isEmpty() ||
                optionBInput.getText().toString().trim().isEmpty() ||
                optionCInput.getText().toString().trim().isEmpty() ||
                optionDInput.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng điền đầy đủ các lựa chọn");
            return false;
        }

        if (correctAnswerGroup.getCheckedRadioButtonId() == -1) {
            showToast("Vui lòng chọn đáp án đúng");
            return false;
        }

        return true;
    }

    private int getCorrectAnswerNumber(int selectedRadioButtonId) {
        if (selectedRadioButtonId == R.id.optionARadio) return 1;
        if (selectedRadioButtonId == R.id.optionBRadio) return 2;
        if (selectedRadioButtonId == R.id.optionCRadio) return 3;
        if (selectedRadioButtonId == R.id.optionDRadio) return 4;
        return -1;
    }

    private void setCorrectAnswerRadio(int correctAnswer) {
        int radioButtonId = -1;
        switch (correctAnswer) {
            case 1: radioButtonId = R.id.optionARadio; break;
            case 2: radioButtonId = R.id.optionBRadio; break;
            case 3: radioButtonId = R.id.optionCRadio; break;
            case 4: radioButtonId = R.id.optionDRadio; break;
        }
        if (radioButtonId != -1) {
            correctAnswerGroup.check(radioButtonId);
        }
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
        questionNumber.setText("Câu hỏi " + currentQuestionIndex);
    }

    private void setQuestionInputsEnabled(boolean enabled) {
        questionInput.setEnabled(enabled);
        optionAInput.setEnabled(enabled);
        optionBInput.setEnabled(enabled);
        optionCInput.setEnabled(enabled);
        optionDInput.setEnabled(enabled);
        correctAnswerGroup.setEnabled(enabled);


        addQuestionButton.setEnabled(enabled);
        deleteQuestionButton.setEnabled(enabled);
    }

    private void setPackInputsEnabled(boolean enabled) {
        questionPackTitleInput.setEnabled(enabled);
        questionPackDescriptionInput.setEnabled(enabled);
        questionPackTopicInput.setEnabled(enabled);
        createPackButton.setEnabled(enabled);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up any resources if needed
    }
}