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
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

/**
 * Fragment quản lý việc tạo bộ câu hỏi và các câu hỏi bên trong.
 * Cho phép giáo viên tạo bộ câu hỏi mới và thêm nhiều câu hỏi với các lựa chọn đáp án.
 */
public class QuestionCreateFragment extends Fragment {
    // Các biến quản lý kết nối database
    private FirebaseDatabase database;            // Instance chính của Firebase database
    private DatabaseReference questionsRef;       // Tham chiếu đến node questions trong Firebase
    private DbContext dbContext;                  // Context của database cục bộ

    // Các trường nhập liệu cho bộ câu hỏi
    private TextInputEditText questionPackTitleInput;      // Nhập tiêu đề bộ câu hỏi
    private TextInputEditText questionPackDescriptionInput; // Nhập mô tả bộ câu hỏi
    private TextInputEditText questionPackTopicInput;      // Nhập chủ đề bộ câu hỏi
    private Button createPackButton;                       // Nút tạo bộ câu hỏi mới

    // Các trường nhập liệu cho từng câu hỏi
    private TextInputEditText questionInput;               // Nhập nội dung câu hỏi
    private TextInputEditText optionAInput;                // Nhập đáp án A
    private TextInputEditText optionBInput;                // Nhập đáp án B
    private TextInputEditText optionCInput;                // Nhập đáp án C
    private TextInputEditText optionDInput;                // Nhập đáp án D
    private RadioGroup correctAnswerGroup;                 // Nhóm radio button chọn đáp án đúng
    private ImageButton addQuestionButton;                 // Nút thêm câu hỏi mới
    private ImageButton deleteQuestionButton;              // Nút xóa câu hỏi hiện tại
    private TextView questionNumber;                       // Hiển thị số thứ tự câu hỏi

    // Các biến quản lý dữ liệu
    private QuestionPack currentQuestionPack;              // Bộ câu hỏi đang được tạo
    private int currentQuestionIndex = 1;                  // Vị trí câu hỏi hiện tại
    private ArrayList<Question> questions = new ArrayList<>(); // Danh sách các câu hỏi

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo các kết nối database
        database = FirebaseDatabase.getInstance();
        questionsRef = database.getReference("questions");
        dbContext = DbContext.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_create, container, false);
        initializeViews(view);
        setupInitialState();
        setupClickListeners();
        TransitionFragemt.initializeMenuNavigation(requireContext(), getParentFragmentManager(), view);
        return view;
    }

    /**
     * Khởi tạo các thành phần giao diện từ layout
     * @param view View gốc của fragment
     */
    private void initializeViews(View view) {
        // Khởi tạo các view cho bộ câu hỏi
        questionPackTitleInput = view.findViewById(R.id.questionPackTitleInput);
        questionPackDescriptionInput = view.findViewById(R.id.questionPackDescriptionInput);
        questionPackTopicInput = view.findViewById(R.id.questionPackTopicInput);
        createPackButton = view.findViewById(R.id.createPackButton);

        // Khởi tạo các view cho câu hỏi
        questionInput = view.findViewById(R.id.questionInput);
        optionAInput = view.findViewById(R.id.optionAInput);
        optionBInput = view.findViewById(R.id.optionBInput);
        optionCInput = view.findViewById(R.id.optionCInput);
        optionDInput = view.findViewById(R.id.optionDInput);
        correctAnswerGroup = view.findViewById(R.id.correctAnswerGroup);
        addQuestionButton = view.findViewById(R.id.AddQuestionButton);
        deleteQuestionButton = view.findViewById(R.id.deleteQuestionButton);
        questionNumber = view.findViewById(R.id.questionNumber);
    }

    /**
     * Thiết lập trạng thái ban đầu của fragment
     */
    private void setupInitialState() {
        setQuestionInputsEnabled(false);  // Vô hiệu hóa phần nhập câu hỏi cho đến khi tạo bộ câu hỏi
        updateQuestionNumber();           // Cập nhật hiển thị số thứ tự câu hỏi
    }

    /**
     * Thiết lập các sự kiện click cho các nút
     */
    private void setupClickListeners() {
        createPackButton.setOnClickListener(v -> createQuestionPack());
        addQuestionButton.setOnClickListener(v -> addNewQuestion());
        deleteQuestionButton.setOnClickListener(v -> deleteQuestion());
    }

    /**
     * Xử lý việc tạo bộ câu hỏi mới
     */
    private void createQuestionPack() {
        String title = questionPackTitleInput.getText().toString().trim();
        String description = questionPackDescriptionInput.getText().toString().trim();
        String topic = questionPackTopicInput.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (title.isEmpty() || description.isEmpty() || topic.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin bộ câu hỏi");
            return;
        }

        if (getContext() == null) return;

        // Lấy ID của giáo viên đang đăng nhập
        String teacherId = SessionManager.getInstance(getContext()).getUserSession().getUser().getUsername();
        if (teacherId == null) {
            showToast("Vui lòng đăng nhập để tạo bộ câu hỏi");
            return;
        }

        // Tạo ID mới cho bộ câu hỏi và khởi tạo đối tượng QuestionPack
        String packId = questionsRef.push().getKey();
        currentQuestionPack = new QuestionPack(
                packId != null ? packId.hashCode() : System.currentTimeMillis(),
                teacherId,
                title,
                description,
                topic
        );

        // Cập nhật giao diện
        setPackInputsEnabled(false);
        setQuestionInputsEnabled(true);
        showToast("Đã tạo bộ câu hỏi. Hãy thêm câu hỏi vào bộ.");
    }

    /**
     * Xử lý việc thêm câu hỏi mới vào bộ câu hỏi
     */
    private void addNewQuestion() {
        if (!validateInputs()) {
            showToast("Vui lòng điền đầy đủ thông tin câu hỏi");
            return;
        }

        Question question = createQuestionFromInputs();
        questions.add(question);
        currentQuestionPack.setQuestions(questions);
        saveQuestionPackToDatabase();

        clearForm();
        currentQuestionIndex++;
        updateQuestionNumber();
        showToast("Đã thêm câu hỏi thành công");
    }

    /**
     * Xử lý việc xóa câu hỏi hiện tại
     */
    private void deleteQuestion() {
        if (currentQuestionIndex > 1) {
            questions.remove(currentQuestionIndex - 1);
            currentQuestionPack.setQuestions(questions);
            saveQuestionPackToDatabase();

            currentQuestionIndex--;
            clearForm();
            updateQuestionNumber();
            showToast("Đã xóa câu hỏi");
        }
    }

    /**
     * Tạo đối tượng Question từ dữ liệu người dùng nhập vào
     * @return Question đối tượng câu hỏi mới
     */
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

    /**
     * Kiểm tra tính hợp lệ của dữ liệu nhập vào
     * @return true nếu dữ liệu hợp lệ, false nếu không
     */
    private boolean validateInputs() {
        if (questionInput.getText().toString().trim().isEmpty() ||
                optionAInput.getText().toString().trim().isEmpty() ||
                optionBInput.getText().toString().trim().isEmpty() ||
                optionCInput.getText().toString().trim().isEmpty() ||
                optionDInput.getText().toString().trim().isEmpty() ||
                correctAnswerGroup.getCheckedRadioButtonId() == -1) {
            return false;
        }
        return true;
    }

    /**
     * Lưu bộ câu hỏi vào database
     */
    private void saveQuestionPackToDatabase() {
        if (currentQuestionPack != null && currentQuestionPack.getId() != 0) {
            questionsRef.child(String.valueOf(currentQuestionPack.getId())).setValue(currentQuestionPack)
                    .addOnSuccessListener(aVoid -> showToast("Đã lưu thành công"))
                    .addOnFailureListener(e -> showToast("Lỗi khi lưu: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật hiển thị số thứ tự câu hỏi
     */
    private void updateQuestionNumber() {
        questionNumber.setText(String.format("Câu hỏi số %d", currentQuestionIndex));
    }

    /**
     * Xóa nội dung các trường nhập liệu
     */
    private void clearForm() {
        questionInput.setText("");
        optionAInput.setText("");
        optionBInput.setText("");
        optionCInput.setText("");
        optionDInput.setText("");
        correctAnswerGroup.clearCheck();
    }

    /**
     * Hiển thị thông báo toast
     * @param message Nội dung thông báo
     */
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Bật/tắt khả năng nhập liệu cho các trường của bộ câu hỏi
     * @param enabled true để bật, false để tắt
     */
    private void setPackInputsEnabled(boolean enabled) {
        questionPackTitleInput.setEnabled(enabled);
        questionPackDescriptionInput.setEnabled(enabled);
        questionPackTopicInput.setEnabled(enabled);
        createPackButton.setEnabled(enabled);
    }

    /**
     * Bật/tắt khả năng nhập liệu cho các trường của câu hỏi
     * @param enabled true để bật, false để tắt
     */
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

    /**
     * Lấy số thứ tự đáp án đúng từ ID của RadioButton được chọn
     * @param radioButtonId ID của RadioButton được chọn
     * @return số thứ tự đáp án (1-4)
     */
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
                return 1; // Mặc định là đáp án A
        }
    }
}