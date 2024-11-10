package com.example.crabquizz.Scripts.Models;


public class User {
    // </editor-fold>
    // <editor-fold desc="Region Description">

    // <editor-fold desc="Region Description">

    private String id;
    private String fullName;
    private String username;
    private String password;
    private String role;

    // </editor-fold>


    // Empty constructor required for Firestore
    public User() {}

    public User(String fullName, String username, String password,String role) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // <editor-fold desc="Getter-Setter">

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String password) {
        this.password = role;
    }
    // </editor-fold>
}