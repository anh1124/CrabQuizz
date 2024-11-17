package com.example.crabquizz.Scripts.Models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class QuestionPack {
    private long id;  // Đổi từ int sang long
    private String teacherId;
    private String title;
    private String description;
    private String topic;
    private String questionsJson;
    private transient List<Question> questions;

    // Constructor
    public QuestionPack(long id, String teacherId, String title, String description, String topic, String questionsJson) {
        this.id = id;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.questionsJson = questionsJson;
    }

    public QuestionPack(long id, String teacherId, String title, String description, String topic) {
        this.id = id;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.questions = new ArrayList<>();
        updateQuestionsJson();
    }

    // Thêm phương thức setQuestions()
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        updateQuestionsJson(); // Cập nhật JSON khi set questions mới
    }

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        questions.add(question);
        updateQuestionsJson();
    }

    public List<Question> getQuestions() {
        if (questions == null && questionsJson != null) {
            loadQuestionsFromJson();
        }
        return questions;
    }

    private void loadQuestionsFromJson() {
        if (questionsJson != null && !questionsJson.isEmpty()) {
            Gson gson = new Gson();
            questions = gson.fromJson(questionsJson, new TypeToken<List<Question>>(){}.getType());
        } else {
            questions = new ArrayList<>();
        }
    }

    private void updateQuestionsJson() {
        Gson gson = new Gson();
        this.questionsJson = gson.toJson(questions);
    }

    // Getters and Setters
    public long getId() {
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
