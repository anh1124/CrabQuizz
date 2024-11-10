package com.example.crabquizz.Scripts.Controller;

import android.util.Log;
import com.example.crabquizz.Scripts.SessionManager;

import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserController {
    private static final String TAG = "UserController";
    private final DbContext dbContext;
    private final SessionManager sessionManager;
    public static UserController instance;

    public UserController(SessionManager sessionManager) {
        this.dbContext = DbContext.getInstance();
        this.sessionManager = sessionManager;
    }
    // Phương thức để lấy instance của UserController (Singleton)
    public static synchronized UserController getInstance(SessionManager sessionManager) {
        if (instance == null) {
            instance = new UserController(sessionManager); // Tạo mới nếu chưa có instance
        }
        return instance;
    }
    public void loginWithUsernameAndToken(String username, String token, LoginCallback callback) {
        // Kiểm tra username và token
        dbContext.getUserByUsernameAndToken(username, token)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Nếu tìm thấy user theo username và token, lấy thông tin chi tiết của user
                        UserController.getInstance(sessionManager).getUserByUsername(username, new LoginCallback() {
                            @Override
                            public void onLoginSuccess(User user) {
                                // Lưu thông tin người dùng và gọi callback thành công
                                sessionManager.saveUserInfo(user);
                                callback.onLoginSuccess(user);
                            }

                            @Override
                            public void onLoginFailed(String errorMessage) {
                                // Nếu có lỗi khi lấy thông tin người dùng, gọi callback thất bại
                                callback.onLoginFailed(errorMessage);
                            }
                        });
                    } else {
                        // Nếu không tìm thấy user, gọi callback thất bại
                        callback.onLoginFailed("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi gọi Firestore, gọi callback thất bại
                    Log.e(TAG, "Login error", e);
                    callback.onLoginFailed(e.getMessage());
                });
    }



    /**
     * Fetches user details by username.
     */
    public void getUserByUsername(String username, LoginCallback callback) {
        dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("username", username)  // Sử dụng whereEqualTo thay vì equals
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Lấy document đầu tiên từ kết quả
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);  // Sử dụng getDocuments() thay vì getDocument()
                        User user = userDoc.toObject(User.class);
                        if (user != null) {
                            callback.onLoginSuccess(user);
                            Log.d(TAG, "User details fetched");
                        } else {
                            callback.onLoginFailed("Failed to convert document to User object");
                        }
                    } else {
                        callback.onLoginFailed("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user", e);
                    callback.onLoginFailed(e.getMessage());
                });
    }

    /**
     * Logs out the current user by clearing session information.
     */
    public void logout() {
        sessionManager.logoutUser();
        Log.d(TAG, "User logged out successfully");
    }

    /**
     * Callback interface for login success or failure.
     */
    public interface LoginCallback {
        void onLoginSuccess(User user);
        void onLoginFailed(String errorMessage);
    }
}
