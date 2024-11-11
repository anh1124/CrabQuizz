package com.example.crabquizz.Scripts.Models;

// Model cho bộ câu hỏi
 class QuestionPack {
    private int id;
    private String teacherId;
    private String title;
    private String questionsJson; // JSON string chứa List<Question>

    // Constructor
    public QuestionPack(int id, String teacherId, String questionsJson) {
        this.id = id;
        this.teacherId = teacherId;
        this.questionsJson = questionsJson;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
}