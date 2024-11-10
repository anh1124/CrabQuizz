package com.example.crabquizz.Scripts.Models;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.ArrayList;

public class DbContext {
    private static DbContext instance;
    public final FirebaseFirestore db;

    // Collections
    //private final String ADMINS_COLLECTION = "admins";
    public final String USERS_COLLECTION = "users";
    public final String QUIZZES_COLLECTION = "quizzes";

    private DbContext() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized DbContext getInstance() {
        if (instance == null) {
            instance = new DbContext();
        }
        return instance;
    }


    public Task<QuerySnapshot> getUserByUsernameAndToken(String username, String token) {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("username", username)
                .whereEqualTo("token", token)
                .get();
    }

    public String getUsersCollection() {
        return USERS_COLLECTION;
    }


    // Generic methods for CRUD operations
    public Task<Void> add(String collection, Object data) {
        return db.collection(collection).document().set(data);
    }

    public Task<DocumentReference> addWithAutoId(String collection, Object data) {
        return db.collection(collection).add(data);
    }

    public Task<Void> update(String collection, String documentId, Object data) {
        return db.collection(collection).document(documentId).set(data);
    }

    public Task<Void> delete(String collection, String documentId) {
        return db.collection(collection).document(documentId).delete();
    }

    public Task<DocumentSnapshot> getById(String collection, String documentId) {
        return db.collection(collection).document(documentId).get();
    }

    public Task<QuerySnapshot> getAll(String collection) {
        return db.collection(collection).get();
    }



    // Helper method for converting QuerySnapshot to List of objects
    public <T> List<T> convertToList(QuerySnapshot querySnapshot, Class<T> valueType) {
        List<T> list = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            T item = document.toObject(valueType);
            list.add(item);
        }
        return list;
    }
}