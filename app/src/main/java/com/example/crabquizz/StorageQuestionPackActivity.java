package com.example.crabquizz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.Scripts.Adapter.QuestionPackAdapter;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.QuestionPack;

import java.util.ArrayList;
import java.util.List;

public class StorageQuestionPackActivity extends AppCompatActivity implements QuestionPackAdapter.OnQuestionPackClickListener {

    private static QuestionPackAdapter.OnQuestionPackClickListener examCallback;
    private RecyclerView questionsRecyclerView;
    private QuestionPackAdapter adapter;
    private List<QuestionPack> questionPacks;
    private DbContext dbContext; // Lưu DbContext vào biến để tái sử dụng

    public static void setExamCallback(QuestionPackAdapter.OnQuestionPackClickListener callback) {
        examCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_storage_question_pack);

        // Khởi tạo DbContext
        dbContext = DbContext.getInstance();

        // Khởi tạo RecyclerView và Adapter
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        questionPacks = new ArrayList<>();
        setupRecyclerView();

        // Tải danh sách bộ câu hỏi từ cơ sở dữ liệu
        loadQuestionPacks();

        // Thêm nút back
        ImageButton backButton = findViewById(R.id.backHomeButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new QuestionPackAdapter(questionPacks, this);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onQuestionPackClick(QuestionPack questionPack) {
        if (examCallback != null) {
            examCallback.onQuestionPackClick(questionPack);
        }
    }

    private void loadQuestionPacks() {
        // Chỉ cần gọi DbContext một lần và tái sử dụng
        dbContext.getAll("questionpacks")
                .addOnSuccessListener(querySnapshots -> {
                    questionPacks.clear();
                    List<QuestionPack> packs = dbContext.convertToList(querySnapshots, QuestionPack.class);
                    questionPacks.addAll(packs);
                    adapter.updateData(questionPacks);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải bộ câu hỏi", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        examCallback = null;  // Giải phóng callback để tránh rò rỉ bộ nhớ
    }
}
