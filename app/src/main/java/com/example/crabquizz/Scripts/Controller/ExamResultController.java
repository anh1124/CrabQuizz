package com.example.crabquizz.Scripts.Controller;

import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.ExamResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
public class ExamResultController {
    private DbContext dbContext;
    private static final String EXAM_RESULTS_COLLECTION = "studentscores";

    public ExamResultController() {
        this.dbContext = DbContext.getInstance();
    }

    // Lấy danh sách StudentScore của một học sinh trong một lớp cụ thể
    public Task<List<ExamResult.StudentScore>> getStudentScoresInClass(int studentId, String classId) {
        return dbContext.db.collection(EXAM_RESULTS_COLLECTION)
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("classId", classId)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    List<ExamResult.StudentScore> scores = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ExamResult.StudentScore score = new ExamResult.StudentScore(
                                document.getString("classId"),
                                document.getLong("studentId").intValue(),
                                document.getString("dateDo"),
                                document.getLong("score").intValue(),
                                document.getString("questionPackId"),
                                document.getString("correctAnswersCount"),
                                document.getString("examTime")
                        );
                        scores.add(score);
                    }
                    return scores;
                });
    }
    public Task<List<ExamResult.StudentScore>> getStudentExamResults(int studentId, String classId) {
        return dbContext.db.collection(EXAM_RESULTS_COLLECTION)
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("classId", classId)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    List<ExamResult.StudentScore> scores = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ExamResult.StudentScore score = new ExamResult.StudentScore(
                                document.getString("classId"),
                                document.getLong("studentId").intValue(),
                                document.getString("dateDo"),
                                document.getLong("score").intValue(),
                                document.getString("questionPackId"),
                                document.getString("correctAnswersCount"),
                                document.getString("examTime")
                        );
                        scores.add(score);
                    }
                    return scores;
                });
    }
    // Lấy danh sách điểm của tất cả học sinh trong một lớp
    public Task<List<ExamResult.StudentScore>> getAllStudentScoresInClass(String classId) {
        return dbContext.db.collection(EXAM_RESULTS_COLLECTION)
                .whereEqualTo("classId", classId)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    List<ExamResult.StudentScore> scores = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        ExamResult.StudentScore score = new ExamResult.StudentScore(
                                document.getString("classId"),
                                document.getLong("studentId").intValue(),
                                document.getString("dateDo"),
                                document.getLong("score").intValue(),
                                document.getString("questionPackId"),
                                document.getString("correctAnswersCount"),
                                document.getString("examTime")
                        );
                        scores.add(score);
                    }
                    return scores;
                });
    }

    // Helper method để thêm kết quả thi mới
    public Task<Void> addExamResult(ExamResult.StudentScore studentScore) {
        Map<String, Object> examData = new HashMap<>();
        examData.put("classId", studentScore.getClassId());
        examData.put("studentId", studentScore.getStudentId());
        examData.put("dateDo", studentScore.getDateDo());
        examData.put("score", studentScore.getScore());
        examData.put("questionPackId", studentScore.getQuestionPackId());
        examData.put("correctAnswersCount", studentScore.getCorrectAnswersCount());
        examData.put("examTime", studentScore.getExamTime());

        return dbContext.add(EXAM_RESULTS_COLLECTION, examData);
    }
}