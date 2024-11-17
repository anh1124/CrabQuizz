package com.example.crabquizz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QuestionCreateScreen extends AppCompatActivity {
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
    private Button saveButton;
    private ImageButton addQuestionButton, deleteQuestionButton;
    private TextView questionNumber;

    // Data
    private QuestionPack currentQuestionPack;
    private int currentQuestionIndex = 1;
    private ArrayList<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question_create_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        questionsRef = database.getReference("questions");
        dbContext = DbContext.getInstance();

        initializeViews();
        setupInitialState();
        setupClickListeners();

        MenuNavigationClickController controller = new MenuNavigationClickController(this);
        controller.setUpAndHandleBottomNavigationView(findViewById(R.id.bottomNavigation));
    }

    private void initializeViews() {
        // Initialize Question Pack Views
        questionPackTitleInput = findViewById(R.id.questionPackTitleInput);
        questionPackDescriptionInput = findViewById(R.id.questionPackDescriptionInput);
        questionPackTopicInput = findViewById(R.id.questionPackTopicInput);
        createPackButton = findViewById(R.id.createPackButton);

        // Initialize Question Views
        questionInput = findViewById(R.id.questionInput);
        optionAInput = findViewById(R.id.optionAInput);
        optionBInput = findViewById(R.id.optionBInput);
        optionCInput = findViewById(R.id.optionCInput);
        optionDInput = findViewById(R.id.optionDInput);
        correctAnswerGroup = findViewById(R.id.correctAnswerGroup);

        addQuestionButton = findViewById(R.id.AddQuestionButton);
        deleteQuestionButton = findViewById(R.id.deleteQuestionButton);
        questionNumber = findViewById(R.id.questionNumber);
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
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin gói câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        String teacherId = SessionManager.getInstance(this).getUserSession().getUser().getUsername();
        if (teacherId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tạo gói câu hỏi", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Đã tạo gói câu hỏi. Hãy thêm câu hỏi vào gói.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, "Đã lưu câu hỏi vào gói", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteQuestion() {
        if (questions.isEmpty() || currentQuestionIndex > questions.size()) {
            clearForm();
            return;
        }

        new AlertDialog.Builder(this)
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
            Toast.makeText(this, "Vui lòng tạo gói câu hỏi trước", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Vui lòng điền đầy đủ các lựa chọn", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (correctAnswerGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn đáp án đúng", Toast.LENGTH_SHORT).show();
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
}