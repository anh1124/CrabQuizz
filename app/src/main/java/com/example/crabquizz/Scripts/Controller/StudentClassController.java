package com.example.crabquizz.Scripts.Controller;
import android.util.Log;
import android.view.Menu;

import com.example.crabquizz.Scripts.Models.StudentClass;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentClassController {
    private static final String TAG = "StudentClassController";
    private DbContext dbContext;
    public static final String CLASS_COLLECTION = "classes";

    public StudentClassController() {
        this.dbContext = DbContext.getInstance();
    }
    public interface CreateClassCallback {
        void onComplete(boolean success, String message);
    }
    // Create a new StudentClass
    public Task<Void> createClass(int teacherId, String className) {
        StudentClass studentClass = new StudentClass();
        studentClass.setTeacherId(teacherId);
        studentClass.setName(className);

        return dbContext.add(CLASS_COLLECTION, studentClass)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Class created successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to create class", e));
    }
    public Task<Integer> getStudentCountForClass(String classId) {
        return dbContext.getById(CLASS_COLLECTION, classId)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        StudentClass studentClass = document.toObject(StudentClass.class);
                        if (studentClass != null) {
                            return studentClass.getStudentCount(); // Trả về số lượng học sinh
                        }
                    } else {
                        Log.e(TAG, "Error fetching class by classId", task.getException());
                    }
                    return 0; // Trả về 0 nếu không tìm thấy lớp hoặc gặp lỗi
                });
    }

    // Method to get all classes for a teacher and return them as JSON
    public Task<String> getTeacherClassesAsJson(int teacherId) {
        return dbContext.db.collection(dbContext.CLASSES_COLLECTION)
                .whereEqualTo("teacherId", teacherId)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Log.e(TAG, "Error fetching teacher classes", task.getException());
                        return "[]";
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    List<StudentClass> studentClasses = new ArrayList<>();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        StudentClass studentClass = new StudentClass();
                        studentClass.setId(document.getId());
                        studentClass.setName(document.getString("name"));

                        // Handle teacherId
                        Object teacherIdObj = document.get("teacherId");
                        if (teacherIdObj != null) {
                            if (teacherIdObj instanceof Long) {
                                studentClass.setTeacherId(((Long) teacherIdObj).intValue());
                            } else if (teacherIdObj instanceof Integer) {
                                studentClass.setTeacherId((Integer) teacherIdObj);
                            }
                        }

                        // Handle studentIds
                        List<Integer> studentIds = new ArrayList<>();
                        List<Object> studentIdsObj = (List<Object>) document.get("studentIds");
                        if (studentIdsObj != null) {
                            for (Object studentId : studentIdsObj) {
                                if (studentId instanceof Long) {
                                    studentIds.add(((Long) studentId).intValue());
                                } else if (studentId instanceof Integer) {
                                    studentIds.add((Integer) studentId);
                                }
                            }
                        }
                        studentClass.setStudentIds(studentIds);

                        studentClasses.add(studentClass);
                    }

                    Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .create();
                    String json = gson.toJson(studentClasses);
                    Log.d(TAG, "Fetched classes JSON: " + json);
                    return json;
                });
    }
    // Add a student to a class
    public Task<Void> addStudentToClass(String classId, int studentId) {
        return dbContext.getById(CLASS_COLLECTION, classId)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        StudentClass studentClass = document.toObject(StudentClass.class);
                        if (studentClass != null) {
                            studentClass.addStudentId(studentId);
                            return dbContext.update(CLASS_COLLECTION, classId, studentClass);
                        }
                    }
                    throw new RuntimeException("Class not found");
                });
    }

    // Remove a student from a class
    public Task<Void> removeStudentFromClass(String classId, int studentId) {
        return dbContext.getById(CLASS_COLLECTION, classId)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        StudentClass studentClass = document.toObject(StudentClass.class);
                        if (studentClass != null) {
                            studentClass.removeStudentId(studentId);
                            return dbContext.update(CLASS_COLLECTION, classId, studentClass);
                        }
                    }
                    throw new RuntimeException("Class not found");
                });
    }
    // Update the class name
    public Task<Void> updateClassName(String classId, String newName) {
        return dbContext.getById(CLASS_COLLECTION, classId)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        StudentClass studentClass = document.toObject(StudentClass.class);
                        if (studentClass != null) {
                            studentClass.setName(newName);
                            return dbContext.update(CLASS_COLLECTION, classId, studentClass);
                        }
                    }
                    throw new RuntimeException("Class not found");
                });
    }

    // Delete a class
    public Task<Void> deleteClass(String classId) {
        return dbContext.delete(CLASS_COLLECTION, classId)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Class deleted successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete class", e));
    }

    // Retrieve all classes (optional)
    public Task<QuerySnapshot> getAllClasses() {
        return dbContext.getAll(CLASS_COLLECTION)
                .addOnSuccessListener(querySnapshot -> Log.d(TAG, "Fetched all classes successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch classes", e));
    }

    /**
     * Lấy danh sách học sinh trong class dựa trên classId
     * @param classId ID của class
     * @return Task chứa danh sách học sinh
     */
    public Task<List<Integer>> getStudentsByClassId(String classId) {
        return dbContext.getById(CLASS_COLLECTION, classId)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        StudentClass studentClass = document.toObject(StudentClass.class);
                        if (studentClass != null) {
                            return studentClass.getStudentIds(); // Lấy danh sách ID học sinh
                        }
                    } else {
                        Log.e(TAG, "Error fetching students by classId", task.getException());
                    }
                    return null; // Trả về null nếu không tìm thấy
                });
    }
    public void checkAndCreateClass(int teacherId, String className, CreateClassCallback callback) {
        // Create a map of the class data
        Map<String, Object> classData = new HashMap<>();
        classData.put("name", className);
        classData.put("teacherId", teacherId);
        classData.put("studentIds", new ArrayList<>()); // Initialize empty student list
        classData.put("setquestionPackIdNowForExam","0");

        // Add the class to Firestore
        dbContext.db.collection(dbContext.CLASSES_COLLECTION) // Use dbContext instance
                .add(classData)
                .addOnSuccessListener(documentReference -> {
                    // Update the document with its ID
                    documentReference.update("id", documentReference.getId())
                            .addOnSuccessListener(aVoid ->
                                    callback.onComplete(true, "Class created successfully"))
                            .addOnFailureListener(e ->
                                    callback.onComplete(false, "Failed to update class ID: " + e.getMessage()));
                })
                .addOnFailureListener(e ->
                        callback.onComplete(false, "Failed to create class: " + e.getMessage()));
    }


    // Method to get all classes for a teacher
    public Task<QuerySnapshot> getTeacherClasses(int teacherId) {
        return dbContext.db.collection(dbContext.CLASSES_COLLECTION)
                .whereEqualTo("teacherId", teacherId)
                .get();
    }

    // Interface để callback kết quả tạo lớp
    public interface OnClassCreationCallback {
        void onClassCreated(boolean success, String message);
    }

    public Task<String> getQuestionPackIdForClass(String classId) {
        return dbContext.getById(CLASS_COLLECTION, classId)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        StudentClass studentClass = document.toObject(StudentClass.class);
                        if (studentClass != null) {
                            return studentClass.getquestionPackIdNowForExam(); // Trả về giá trị questionPackIdNowForExam
                        }
                    }
                    Log.e(TAG, "Error fetching questionPackId for classId: " + classId, task.getException());
                    return "0"; // Trả về "0" nếu không tìm thấy hoặc gặp lỗi
                });
    }
    public Task<StudentClass> getClassById(String classId) {
        return dbContext.getById(CLASS_COLLECTION, classId)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        StudentClass studentClass = document.toObject(StudentClass.class);
                        if (studentClass != null) {
                            studentClass.setId(classId); // Đảm bảo gán ID từ Firestore vào đối tượng
                            return studentClass;
                        }
                    }
                    Log.e(TAG, "Class not found for classId: " + classId, task.getException());
                    throw new RuntimeException("Class not found.");
                });
    }





}