package com.example.crabquizz.Scripts.Models;

public class QuestionPack {
    private String id;
    private String teacherId;
    private String title;
    private String description;
    private String topic;
    private String questionJson;


    public QuestionPack() {
    }

    public QuestionPack(String id, String teacherId, String title, String description, String topic, String questionJson) {
        this.id = id;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.questionJson = questionJson;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getQuestionJson() {
        return questionJson;
    }

    public void setQuestionJson(String questionJson) {
        this.questionJson = questionJson;
    }
}