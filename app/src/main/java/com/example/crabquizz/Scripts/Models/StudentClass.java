package com.example.crabquizz.Scripts.Models;

import java.util.ArrayList;
import java.util.List;

public class StudentClass {
    private String id;  // Change to String instead of int/double
    private String name;
    private int teacherId;
    private List<Integer> studentIds;
    private String questionPackIdNowForExam;

    public StudentClass() {
        // Required empty constructor for Firestore
        studentIds = new ArrayList<>();
        questionPackIdNowForExam = "";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public List<Integer> getStudentIds() {
        return studentIds != null ? studentIds : new ArrayList<>();
    }

    public void setStudentIds(List<Integer> studentIds) {
        this.studentIds = studentIds;
    }

    public String getquestionPackIdNowForExam() {
        return questionPackIdNowForExam;
    }

    public void setquestionPackIdNowForExam(String questionPackIdNowForExam) {
        this.questionPackIdNowForExam = questionPackIdNowForExam;
    }



    public int getStudentCount() {
        return studentIds != null ? studentIds.size() : 0;
    }

    public void addStudentId(int studentId) {
        if (studentIds == null) {
            studentIds = new ArrayList<>();
        }
        if (!studentIds.contains(studentId)) {
            studentIds.add(studentId);
        }
    }

    public void removeStudentId(int studentId) {
        if (studentIds != null) {
            studentIds.remove(Integer.valueOf(studentId));
        }
    }

}
