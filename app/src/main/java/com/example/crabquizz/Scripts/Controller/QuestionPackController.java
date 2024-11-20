package com.example.crabquizz.Scripts.Controller;

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
        return dbContext.getById("questionpacks", questionPackId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            return document.toObject(QuestionPack.class);
                        } else {
                            throw new Exception("Question Pack không tồn tại");
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }
}