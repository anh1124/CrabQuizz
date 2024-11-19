package com.example.crabquizz.Scripts.Models;


import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class DbContext {
    private static DbContext instance;
    public final FirebaseFirestore db;

    // Collections

    public final String USERS_COLLECTION = "users";
    public final String CLASSES_COLLECTION = "classes";


    public final String APP_SETUP = "appsetup";
    public final String APP_SETUP_DOC_ID = "4DC2e4QEjy4tMtHjYUUi";
    //cái appsetup phải giữ document
    private DbContext() {
        db = FirebaseFirestore.getInstance();
    }


    public static synchronized DbContext getInstance() {
        if (instance == null) {
            instance = new DbContext();
        }
        return instance;
    }


    public Task<Integer> fetchMaxTokenValidityDays() {
        return db.collection(APP_SETUP)
                .document(APP_SETUP_DOC_ID)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Parse and return maxTokenValidityDays from the document
                        DocumentSnapshot document = task.getResult();
                        return document.getLong("maxTokenValidityDays").intValue();
                    } else {
                        throw task.getException(); // Handle exception as needed
                    }
                });
    }






    public String getUsersCollection() {
        return USERS_COLLECTION;
    }

    public Task<QuerySnapshot> query(String collection, String field, Object value) {
        return FirebaseFirestore.getInstance()
                .collection(collection)
                .whereEqualTo(field, value)
                .get();
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