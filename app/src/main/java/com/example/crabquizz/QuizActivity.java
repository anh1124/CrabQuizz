package com.example.crabquizz;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.Scripts.Adapter.QuestionPackAdapter;
import com.example.crabquizz.Scripts.Adapter.QuestionPackForJoinAdapter;
import com.example.crabquizz.Scripts.Controller.QuestionPackController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements QuestionPackForJoinAdapter.OnQuestionPackClickListener {
    private ImageButton backButton;
    private DbContext dbContext;
    private List<Question> questions;
    private QuestionPackForJoinAdapter adapter;
    private RecyclerView recyclerViewQuestions;
    private List<QuestionPack> questionPacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Khởi tạo đối tượng DbContext và danh sách câu hỏi
        dbContext = DbContext.getInstance();
        questions = new ArrayList<>();
        questionPacks = new ArrayList<>();


        initializeViews();
        setUpRecycler();
        setOnClick();
        loadQuestion();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.BackButton);
        recyclerViewQuestions = findViewById(R.id.recyclerViewQuestionsAnswer);

        // Cài đặt RecyclerView

    }
    private void setUpRecycler() {
        adapter = new QuestionPackForJoinAdapter(questions, this);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewQuestions.setAdapter(adapter);
    }

    private void setOnClick() {
        backButton.setOnClickListener(v -> finish());
    }

    private void loadQuestion() {
        String packId = getIntent().getStringExtra("packId");
        String packQuestionJson = getIntent().getStringExtra("packQuestionJson");

        questions.clear();

        // Parse JSON thành danh sách câu hỏi
        Gson gson = new Gson();
        Type questionListType = new TypeToken<List<Question>>(){}.getType();
        List<Question> questionList = gson.fromJson(packQuestionJson, questionListType);

        // Log số lượng câu hỏi
        Log.d("DEBUGgg", "Số lượng câu hỏi: " + (questionList != null ? questionList.size() : 0));

        // Cập nhật danh sách câu hỏi
        if (questionList != null) {
            questions.addAll(questionList);
            adapter.updateQuestions(questions);
        } else {
            Toast.makeText(QuizActivity.this, "Không có câu hỏi trong bộ này", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onQuestionPackClick(QuestionPack questionPack) {

    }
}
