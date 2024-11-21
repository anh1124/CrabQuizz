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
    private static final String EXAM_RESULTS_COLLECTION = "examresults";

    public ExamResultController() {
        this.dbContext = DbContext.getInstance();
    }

    public Task<List<ExamResult.StudentScore>> getStudentExamResults(int studentId, String classId) {
        return dbContext.query(EXAM_RESULTS_COLLECTION, "classId", classId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Lọc và trả về danh sách kết quả thi của học sinh cụ thể
                        List<ExamResult.StudentScore> studentScores = new ArrayList<>();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            ExamResult examResult = document.toObject(ExamResult.class);
                            if (examResult != null) {
                                // Lọc các kết quả của học sinh theo studentId
                                List<ExamResult.StudentScore> filteredScores = examResult.getScores().stream()
                                        .filter(score -> score.getStudentId() == studentId)
                                        .collect(Collectors.toList());

                                studentScores.addAll(filteredScores);
                            }
                        }

                        return studentScores;
                    } else {
                        throw task.getException();
                    }
                });
    }
    // New method to get student scores with names for a specific class
    public Task<Map<String, Double>> getStudentScoresWithNames(String classId) {
        return dbContext.query(EXAM_RESULTS_COLLECTION, "classId", classId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        Map<String, Double> studentScores = new HashMap<>();

                        // Fetch students in this class
                        Task<QuerySnapshot> studentsTask = dbContext.query("STUDENTS", "classId", classId);
                        QuerySnapshot studentsSnapshot = Tasks.await(studentsTask);

                        // Create a map of student IDs to names
                        Map<Integer, String> studentIdToNameMap = new HashMap<>();
                        for (DocumentSnapshot studentDoc : studentsSnapshot.getDocuments()) {
                            int studentId = studentDoc.getLong("id").intValue();
                            String studentName = studentDoc.getString("name");
                            studentIdToNameMap.put(studentId, studentName);
                        }

                        // Process exam results
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            ExamResult examResult = document.toObject(ExamResult.class);
                            if (examResult != null) {
                                for (ExamResult.StudentScore scoreEntry : examResult.getScores()) {
                                    String studentName = studentIdToNameMap.get(scoreEntry.getStudentId());
                                    if (studentName != null) {
                                        // If multiple exam results exist, take the highest score
                                        studentScores.merge(studentName, scoreEntry.getScore(),
                                                (existingScore, newScore) -> Math.max(existingScore, newScore));
                                    }
                                }
                            }
                        }

                        return studentScores;
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Thêm kết quả thi mới
    public Task<Void> addExamResult(ExamResult examResult) {
        return dbContext.add(EXAM_RESULTS_COLLECTION, examResult);
    }

    // Lấy kết quả thi theo ID
    public Task<ExamResult> getExamResultById(String examResultId) {
        return dbContext.getById(EXAM_RESULTS_COLLECTION, examResultId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            return document.toObject(ExamResult.class);
                        } else {
                            throw new Exception("Exam Result không tồn tại");
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Lấy tất cả kết quả thi
    public Task<List<ExamResult>> getAllExamResults() {
        return dbContext.getAll(EXAM_RESULTS_COLLECTION)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        return dbContext.convertToList(querySnapshot, ExamResult.class);
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Lấy kết quả thi theo ID lớp
    public Task<List<ExamResult>> getExamResultsByClassId(String classId) {
        return dbContext.query(EXAM_RESULTS_COLLECTION, "classId", classId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        return dbContext.convertToList(querySnapshot, ExamResult.class);
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Cập nhật kết quả thi
    public Task<Void> updateExamResult(String examResultId, ExamResult examResult) {
        return dbContext.update(EXAM_RESULTS_COLLECTION, examResultId, examResult);
    }

    // Xóa kết quả thi
    public Task<Void> deleteExamResult(String examResultId) {
        return dbContext.delete(EXAM_RESULTS_COLLECTION, examResultId);
    }
    // Phương thức kiểm tra học sinh đã thi hay chưa
    public Task<Boolean> hasStudentTakenExam(int studentId, String classId, String questionPackId) {
        return dbContext.query(EXAM_RESULTS_COLLECTION, "classId", classId)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Duyệt qua các bản ghi kết quả thi trong lớp
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            ExamResult examResult = document.toObject(ExamResult.class);
                            if (examResult != null) {
                                // Kiểm tra xem có kết quả thi của học sinh này với đúng bộ câu hỏi không
                                boolean hasExamResult = examResult.getScores().stream()
                                        .anyMatch(score ->
                                                score.getStudentId() == studentId &&
                                                        score.getQuestionPackId().equals(questionPackId)
                                        );

                                if (hasExamResult) {
                                    return true; // Học sinh đã thi
                                }
                            }
                        }

                        return false; // Không tìm thấy kết quả thi
                    } else {
                        throw task.getException();
                    }
                });
    }


}