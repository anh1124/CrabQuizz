package com.example.crabquizz.Scripts.Models;

public class QuestionAnswer {
    private int id;
    private int answer;

    // Constructor
    public QuestionAnswer(int id, int answer) {
        this.id = id;
        this.answer = answer;
    }

    public QuestionAnswer() {

    }

    // Getter for id
    public int getId() {
        return id;
    }

    // Setter for id
    public void setId(int id) {
        this.id = id;
    }

    // Getter for answer
    public int getAnswer() {
        return answer;
    }

    // Setter for answer
    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "QuestionAnswer{" +
                "id=" + id +
                ", answer=" + answer +
                '}';
    }
}
