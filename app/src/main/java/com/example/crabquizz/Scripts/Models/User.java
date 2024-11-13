package com.example.crabquizz.Scripts.Models;

import java.util.Date;
public class User {

    // <editor-fold desc="Region Description">

    private int id;
    private String fullName;
    private String username;
    private String password;
    private String role;//student or teacher or guess
    private String token;
    private String email;
    private Date tokenExpiredAt;
    // </editor-fold>

    // Empty constructor required for Firestore
    public User() {}

    // Constructor used in getSharedPreferencesUserDetails()
    public User(String fullName, String username, String token, String role) {
        this.fullName = fullName;
        this.username = username;
        this.token = token;
        this.role = role;
    }
    public User( int id,String fullName, String username, String password, String role, String token, String email, Date tokenExpiredAt) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.token = token;
        this.email = email;
        this.tokenExpiredAt = tokenExpiredAt;
    }

    // <editor-fold desc="Getter-Setter">

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void setRole(String role) {
        this.role = role;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Date getTokenExpiredAt() {
        return tokenExpiredAt;
    }
    public void setTokenExpiredAt(Date tokenExpiredAt) {
        this.tokenExpiredAt = tokenExpiredAt;
    }
    // </editor-fold>
}