package com.example.crabquizz;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Adapter.ExamResultAdapter;
import com.example.crabquizz.Scripts.Controller.ExamResultController;
import com.example.crabquizz.Scripts.Models.ExamResult;

import java.util.List;

public class StudentExamResultsActivity extends AppCompatActivity {
    // Các hằng để truyền dữ liệu giữa các activity
    public static final String EXTRA_STUDENT_ID = "extra_student_id";
    public static final String EXTRA_CLASS_ID = "extra_class_id";

    // Các view trong layout
    private RecyclerView recyclerViewExamResults;
    private TextView tvAverageScore;
    private TextView tvTotalExams;

    // Controller và Adapter
    private ExamResultController examResultController;
    private ExamResultAdapter examResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_exam_results);

        // Ánh xạ view
        recyclerViewExamResults = findViewById(R.id.recyclerViewExamResults);

        // Lấy dữ liệu từ Intent
        int studentId = getIntent().getIntExtra(EXTRA_STUDENT_ID, -1);
        String classId = getIntent().getStringExtra(EXTRA_CLASS_ID);

        // Ghi log thông tin truyền vào
        Log.d("StudentExamResults12", "Received studentId: " + studentId);
        Log.d("StudentExamResults12", "Received classId: " + classId);

        // Kiểm tra dữ liệu
        if (studentId == -1 || classId == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo controller
        examResultController = new ExamResultController();

        // Lấy kết quả thi từ controller
        examResultController.getStudentScoresInClass(studentId, classId)
                .addOnSuccessListener(this::setupExamResults)
                .addOnFailureListener(this::handleError);
    }

    private void setupExamResults(List<ExamResult.StudentScore> examResults) {
        // Ghi log danh sách điểm
        for (ExamResult.StudentScore score : examResults) {
            Log.d("StudentExamResults12", "StudentScore: " +
                    "classId=" + score.getClassId() +
                    ", studentId=" + score.getStudentId() +
                    ", dateDo=" + score.getDateDo() +
                    ", score=" + score.getScore() +
                    ", questionPackId=" + score.getQuestionPackId() +
                    ", correctAnswersCount=" + score.getCorrectAnswersCount() +
                    ", examTime=" + score.getExamTime());
        }

        // Thiết lập RecyclerView
        recyclerViewExamResults.setLayoutManager(new LinearLayoutManager(this));
        examResultAdapter = new ExamResultAdapter(examResults);
        recyclerViewExamResults.setAdapter(examResultAdapter);

        // Tính và hiển thị điểm trung bình và tổng số bài thi (nếu cần)
        if (examResults != null && !examResults.isEmpty()) {
            double totalScore = 0;
            for (ExamResult.StudentScore score : examResults) {
                totalScore += score.getScore();
            }
            double averageScore = totalScore / examResults.size();

            if (tvAverageScore != null) {
                tvAverageScore.setText(String.format("Điểm trung bình: %.1f", averageScore));
            }
            if (tvTotalExams != null) {
                tvTotalExams.setText("Tổng số bài thi: " + examResults.size());
            }
        }
    }
    private void handleError(Exception e) {
        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}