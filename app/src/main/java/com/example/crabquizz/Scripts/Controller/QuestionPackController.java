package com.example.crabquizz.Scripts.Controller;

import android.util.Log;

import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuestionPackController {
    private DbContext dbContext;

    public QuestionPackController() {
        this.dbContext = DbContext.getInstance();
    }

    // Lấy một Question Pack theo ID
    public Task<QuestionPack> getQuestionPackById(String questionPackId) {
        return dbContext.query("questionpacks", "id", questionPackId)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("QuestionPack", "Task failed", task.getException());
                        throw task.getException();
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) {
                        Log.e("QuestionPack", "No QuestionPack found with ID: " + questionPackId);
                        throw new Exception("Question Pack không tồn tại");
                    }

                    // Get the first (and should be only) document
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                    // Log all fields in the document
                    Log.d("QuestionPack", "Document data:");
                    for (String field : document.getData().keySet()) {
                        Object value = document.get(field);
                        Log.d("QuestionPack", field + ": " + (value != null ? value.toString() : "null"));
                    }

                    QuestionPack questionPack = document.toObject(QuestionPack.class);
                    if (questionPack == null) {
                        Log.e("QuestionPack", "Failed to convert document to QuestionPack object");
                        throw new Exception("Lỗi khi chuyển đổi dữ liệu thành QuestionPack");
                    }

                    // Log QuestionPack object after conversion
                    Log.d("QuestionPack", "Converted QuestionPack:");
                    Log.d("QuestionPack", "ID: " + questionPack.getId());
                    Log.d("QuestionPack", "QuestionJson length: " +
                            (questionPack.getQuestionJson() != null ? questionPack.getQuestionJson().length() : "null"));

                    return questionPack;
                });
    }
    public Task<String> getQuestionJsonByQuestionPackId(String questionPackId) {
        return dbContext.getById("questionpacks", questionPackId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            QuestionPack questionPack = document.toObject(QuestionPack.class);
                            if (questionPack != null) {
                                return questionPack.getQuestionJson();
                            } else {
                                throw new Exception("Lỗi khi chuyển đổi dữ liệu thành QuestionPack");
                            }
                        } else {
                            throw new Exception("Không tìm thấy QuestionPack với ID: " + questionPackId);
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }
}