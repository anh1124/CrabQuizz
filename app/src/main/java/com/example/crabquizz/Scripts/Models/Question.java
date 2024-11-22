package com.example.crabquizz.Scripts.Models;

public class Question {
    private int id;//id in
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correctAnswer; // 1-A, 2-B, 3-C, 4-D
    private int selectedOption = -1;

    public Question() {
    }
    // Constructor đầy đủ tham số
    public Question(int id, String question, String answer1, String answer2,
                    String answer3, String answer4, int correctAnswer) {
        this.id = id;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
    }

    // Constructor với 2 câu trả lời (đặt answer3 và answer4 là chuỗi rỗng)
    public Question(int id, String question, String answer1, String answer2, int correctAnswer) {
        this(id, question, answer1, answer2, "", "", correctAnswer);
    }

    // Constructor với 3 câu trả lời (đặt answer4 là chuỗi rỗng)
    public Question(int id, String question, String answer1, String answer2, String answer3, int correctAnswer) {
        this(id, question, answer1, answer2, answer3, "", correctAnswer);
    }
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    public int getSelectedOption() {
        return selectedOption;
    }

    // Setter for selected option
    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }
}