package com.example.crabquizz;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.Scripts.Adapter.ClassScoresAdapter;
import com.example.crabquizz.Scripts.Controller.ExamResultController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.ExamResult;

import java.util.List;

public class ClassScoresActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView tvAverageScore;
    private TextView tvTotalExams;
    private RecyclerView recyclerViewExamResults;

    private ExamResultController examResultController;
    private ClassScoresAdapter scoresAdapter;
    private String classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_scores);

        // Initialize controllers
        examResultController = new ExamResultController();

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        tvTotalExams = findViewById(R.id.tvTotalExams);
        recyclerViewExamResults = findViewById(R.id.recyclerViewExamResults);

        // Get class details from intent
        classId = getIntent().getStringExtra("CLASS_ID");
        className = getIntent().getStringExtra("CLASS_NAME");

        // Set title
        titleTextView.setText("Điểm số lớp " + className);

        // Setup RecyclerView
        recyclerViewExamResults.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display scores
        fetchClassScores();
    }

    private void fetchClassScores() {
        examResultController.getAllStudentScoresInClass(classId)
                .addOnSuccessListener(this::updateUI)
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Lỗi khi tải điểm: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void updateUI(List<ExamResult.StudentScore> scores) {
        // Calculate statistics
        if (scores != null && !scores.isEmpty()) {
            double totalScore = 0;
            for (ExamResult.StudentScore score : scores) {
                totalScore += score.getScore();
            }
            double averageScore = totalScore / scores.size();

            // Update UI
            tvAverageScore.setText(String.format("Điểm trung bình: %.1f", averageScore));
            tvTotalExams.setText("Tổng số bài thi: " + scores.size());

            // Setup adapter
            scoresAdapter = new ClassScoresAdapter(scores);
            recyclerViewExamResults.setAdapter(scoresAdapter);
        } else {
            tvAverageScore.setText("Chưa có điểm");
            tvTotalExams.setText("Tổng số bài thi: 0");
        }
    }
}