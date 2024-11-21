package com.example.crabquizz.Scripts.Models;

public class StudentScore {
    private String name;
    private double score;

    public StudentScore() {
        // Empty constructor for Firestore
    }

    public StudentScore(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}