package com.example.crabquizz.Scripts.Models;

public class QuestionPack {
    private int id;
    private String teacherId;
    private String title;
    private String description;
    private String topic;
    private String questionsJson; // JSON string containing List<Question>

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
}
