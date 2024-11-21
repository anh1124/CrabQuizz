package com.example.crabquizz;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Adapter.ClassScoresAdapter;
import com.example.crabquizz.Scripts.Controller.ExamResultController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// In ClassScoresActivity.java
public class ClassScoresActivity extends AppCompatActivity {
    private RecyclerView recyclerViewScores;
    private TextView titleTextView;
    private ClassScoresAdapter scoresAdapter;
    private List<StudentScore> studentScores;
    private ExamResultController examResultController;
    private DbContext dbContext;
    private String classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_scores);

        // Initialize controllers and context
        examResultController = new ExamResultController();
        dbContext = DbContext.getInstance();

        // Initialize views
        recyclerViewScores = findViewById(R.id.recyclerViewScores);
        titleTextView = findViewById(R.id.titleTextView);

        // Get class details from intent
        classId = getIntent().getStringExtra("CLASS_ID");
        className = getIntent().getStringExtra("CLASS_NAME");

        // Set title
        titleTextView.setText("Điểm số lớp " + className);

        // Initialize data structures
        studentScores = new ArrayList<>();
        scoresAdapter = new ClassScoresAdapter(studentScores);

        // Setup RecyclerView
        recyclerViewScores.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewScores.setAdapter(scoresAdapter);

        // Fetch and display scores
        fetchClassScores();
    }

    private void fetchClassScores() {
        // Add a method to ExamResultController to get student scores with names
        examResultController.getStudentScoresWithNames(classId)
                .addOnSuccessListener(studentScoreMap -> {
                    studentScores.clear();
                    for (Map.Entry<String, Double> entry : studentScoreMap.entrySet()) {
                        studentScores.add(new StudentScore(entry.getKey(), entry.getValue()));
                    }
                    scoresAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("ClassScoresActivity", "Error fetching student scores", e);
                    Toast.makeText(this, "Lỗi khi tải điểm số", Toast.LENGTH_SHORT).show();
                });
    }

    // Inner class to represent student scores (keep this or use the one from ExamResult)
    public static class StudentScore {
        private String name;
        private double score;

        public StudentScore(String name, double score) {
            this.name = name;
            this.score = score;
        }

        public String getName() { return name; }
        public double getScore() { return score; }
    }
}