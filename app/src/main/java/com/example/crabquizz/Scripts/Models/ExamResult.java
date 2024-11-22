package com.example.crabquizz.Scripts.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamResult {
    // Inner class to represent a student's score
    public static class StudentScore {
        private int studentId; // ID của học sinh
        private String dateDo; // Ngày thi
        private int score; // Điểm số của học sinh
        private String questionPackId;
        private String correctAnswersCount;
        private String examTime;

        // Constructors
        public StudentScore() {}

        public StudentScore(int studentId, String dateDo, int score,
                            String questionPackId, String correctAnswersCount, String examTime) {
            this.studentId = studentId;
            this.dateDo = dateDo;
            this.score = score;
            this.questionPackId = questionPackId;
            this.correctAnswersCount = correctAnswersCount;
            this.examTime = examTime;
        }

        // Getters
        public int getStudentId() {
            return studentId;
        }

        public String getDateDo() {
            return dateDo;
        }

        public double getScore() {
            return score;
        }

        public String getQuestionPackId() {
            return questionPackId;
        }

        public String getCorrectAnswersCount() {
            return correctAnswersCount;
        }

        public String getExamTime() {
            return examTime;
        }

        // Setters
        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public void setDateDo(String dateDo) {
            this.dateDo = dateDo;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public void setQuestionPackId(String questionPackId) {
            this.questionPackId = questionPackId;
        }

        public void setCorrectAnswersCount(String correctAnswersCount) {
            this.correctAnswersCount = correctAnswersCount;
        }

        public void setExamTime(String examTime) {
            this.examTime = examTime;
        }
    }

    private String id; // ID duy nhất cho bản ghi điểm
    private String classId; // ID của lớp
    private List<StudentScore> scores; // Danh sách điểm số của từng học sinh

    // Constructors
    public ExamResult() {
        this.scores = new ArrayList<>();
    }

    public ExamResult(String id, String classId) {
        this.id = id;
        this.classId = classId;
        this.scores = new ArrayList<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public List<StudentScore> getScores() {
        return scores;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setScores(List<StudentScore> scores) {
        this.scores = scores;
    }

    // Utility method to add a score
    public void addScore(StudentScore score) {
        if (this.scores == null) {
            this.scores = new ArrayList<>();
        }
        this.scores.add(score);
    }
}