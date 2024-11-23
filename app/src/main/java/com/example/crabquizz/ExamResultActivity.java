package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;

public class ExamResultActivity extends AppCompatActivity {
    // Card View
    private CardView resultCardView;

    // TextViews
    private TextView tvResultTitle, tvQuestionName, tvCorrectAnswers, tvCorrectAnswersValue,
            tvTotalTime, tvTotalTimeValue, tvScoreLabel, tvScoreValue;

    // Button
    private MaterialButton btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_result);

        initializeView();
        loadExamResult();
        setupListeners();
    }

    /**
     * Khởi tạo ánh xạ các view từ layout
     */
    private void initializeView() {
        resultCardView = findViewById(R.id.resultCardView);

        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvQuestionName = findViewById(R.id.tvQuestionName);
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvCorrectAnswersValue = findViewById(R.id.tvCorrectAnswersValue);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTotalTimeValue = findViewById(R.id.tvTotalTimeValue);
        tvScoreLabel = findViewById(R.id.tvScoreLabel);
        tvScoreValue = findViewById(R.id.tvScoreValue);

        btnConfirm = findViewById(R.id.btnConfirm);
    }

    /**
     * Tải kết quả bài thi từ Intent và hiển thị dữ liệu
     */
    private void loadExamResult() {
        // Lấy dữ liệu từ Intent
        String questionPackTitle = getIntent().getStringExtra("quesionPackTitle");
        String correctAnswer = getIntent().getStringExtra("correctAnswer");
        String examTime = getIntent().getStringExtra("examtime");
        String score = getIntent().getStringExtra("score");

        // Kiểm tra null để tránh crash
        tvQuestionName.setText("Bộ câu hỏi: " + (questionPackTitle != null ? questionPackTitle : "N/A"));
        tvCorrectAnswersValue.setText(correctAnswer != null ? correctAnswer : "0");
        tvTotalTimeValue.setText(examTime != null ? examTime : "0:00");
        tvScoreValue.setText((score != null ? score : "0") + "/100");
    }

    /**
     * Cài đặt các listener cho view
     */
    private void setupListeners() {
        // Đóng Activity khi nhấn nút Confirm
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentQuestionViewFragment studentQuestionViewFragment = new StudentQuestionViewFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, studentQuestionViewFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
