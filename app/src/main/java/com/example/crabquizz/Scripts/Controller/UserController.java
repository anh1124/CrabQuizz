package com.example.crabquizz.Scripts.Controller;

import android.util.Log;
import android.widget.Toast;

import com.example.crabquizz.Register;
import com.example.crabquizz.Scripts.SessionManager;
import com.example.crabquizz.Scripts.Models.User;
import com.example.crabquizz.Scripts.Models.AppSetup;

import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.UUID;

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

    public Task<QuerySnapshot> getUserByUsernameAndToken(String username, String token) {
        return dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("username", username)
                .whereEqualTo("token", token)
                .get();
    }

    public Task<String> getHighestUserId() {
        return dbContext.db.collection(dbContext.getUsersCollection())
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Get the first document which has the highest ID
                        DocumentSnapshot highestIdDoc = task.getResult().getDocuments().get(0);
                        // Sử dụng getLong() để lấy giá trị trường "id" (khi "id" là kiểu số nguyên)
                        Long highestId = highestIdDoc.getLong("id");
                        if (highestId != null) {
                            return String.valueOf(highestId); // Chuyển đổi giá trị Long thành String
                        } else {
                            return null; // Trường id là null
                        }
                    } else {
                        return null; // Không tìm thấy tài liệu
                    }
                });
    }

    public Task<Boolean> isEmailUnique(String email) {
        return dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("email", email)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Kiểm tra nếu không có tài liệu nào chứa email này (tức là email duy nhất)
                        return task.getResult().isEmpty();
                    } else {
                        throw task.getException(); // Ném ngoại lệ nếu có lỗi xảy ra
                    }
                });
    }



    public void loginWithUsernameAndToken(String username, String token, LoginCallback callback) {
        dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("username", username)
                .whereEqualTo("token", token)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        User user = userDoc.toObject(User.class);
                        if (user != null) {
                            // Update user's token if needed
                            if (!user.getToken().equals(token)) {
                                user.setToken(token);
                                userDoc.getReference().update("token", token)
                                        .addOnSuccessListener(aVoid -> {
                                            sessionManager.saveUserInfo(user);
                                            callback.onLoginSuccess(user);
                                            Log.d(TAG, "User logged in successfully");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Token update error", e);
                                            callback.onLoginFailed(e.getMessage());
                                        });
                            } else {
                                sessionManager.saveUserInfo(user);
                                callback.onLoginSuccess(user);
                                Log.d(TAG, "User logged in successfully");
                            }
                        } else {
                            callback.onLoginFailed("Failed to convert document to User object");
                        }
                    } else {
                        callback.onLoginFailed("Invalid username or token");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login error", e);
                    callback.onLoginFailed(e.getMessage());
                });
    }


    // New method for user registration
    // User registration method in UserController
    // Phương thức đăng ký người dùng mới
    public void register(String fullName, String username, String password, String role, String email, RegisterCallback callback) {
        // Kiểm tra xem username đã tồn tại trong database hay chưa
        dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Nếu tìm thấy kết quả (tức là username đã tồn tại)
                    if (!querySnapshot.isEmpty()) {
                        // Gọi hàm callback báo lỗi đăng ký vì username đã tồn tại
                        callback.onRegisterFailed("Username already exists");
                        return;
                    }
                    // Nếu username chưa tồn tại, kiểm tra email
                    isEmailUnique(email).addOnSuccessListener(isUnique -> {
                        if (!isUnique) {
                            // Nếu email không duy nhất, gọi callback báo lỗi
                            callback.onRegisterFailed("Email already exists");
                            return;
                        }
                        // Lấy ID người dùng cao nhất hiện tại
                        getHighestUserId().addOnSuccessListener(highestId -> {

                            // Chuyển đổi highestId thành số nguyên hoặc đặt giá trị mặc định nếu không có highestId
                            int id;
                            if (highestId != null) {
                                id = Integer.parseInt(highestId) + 1;
                            } else {
                                id = 1;
                            }
                            // Nếu cả username và email đều duy nhất, tạo đối tượng User mới
                            User newUser = new User(id, fullName, username, password, role, null, email, null);

                            // Thêm người dùng mới vào collection trong database
                            dbContext.add(dbContext.getUsersCollection(), newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        // Nếu thêm thành công, ghi log và gọi callback báo thành công
                                        Log.d(TAG, "User registered successfully");
                                        callback.onRegisterSuccess(newUser);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Nếu có lỗi khi thêm vào database, ghi log lỗi và gọi callback báo lỗi
                                        Log.e(TAG, "Registration error", e);
                                        callback.onRegisterFailed(e.getMessage());
                                    });
                        }).addOnFailureListener(e -> {
                            // Nếu có lỗi khi lấy ID người dùng cao nhất, ghi log lỗi và gọi callback báo lỗi
                            Log.e(TAG, "Error getting highest user ID", e);
                            callback.onRegisterFailed(e.getMessage());
                        });

                    }).addOnFailureListener(e -> {
                        // Nếu có lỗi khi kiểm tra email trong database, ghi log lỗi và gọi callback báo lỗi
                        Log.e(TAG, "Email check error", e);
                        callback.onRegisterFailed(e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    // Nếu có lỗi khi kiểm tra username trong database, ghi log lỗi và gọi callback báo lỗi
                    Log.e(TAG, "Username check error", e);
                    callback.onRegisterFailed(e.getMessage());
                });
    }



    // New method for username/password login
    public void loginWithUsernameAndPassword(String username, String password, LoginCallback callback) {
        dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        User user = userDoc.toObject(User.class);
                        if (user != null) {
                            // Update user's token
                            String newToken = UUID.randomUUID().toString();
                            user.setToken(newToken);

                            // Update token in database
                            userDoc.getReference().update("token", newToken)
                                    .addOnSuccessListener(aVoid -> {
                                        sessionManager.saveUserInfo(user);
                                        callback.onLoginSuccess(user);
                                        Log.d(TAG, "User logged in successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Token update error", e);
                                        callback.onLoginFailed(e.getMessage());
                                    });
                        } else {
                            callback.onLoginFailed("Failed to convert document to User object");
                        }
                    } else {
                        callback.onLoginFailed("Invalid username or password");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login error", e);
                    callback.onLoginFailed(e.getMessage());
                });
    }

    /**
     * Fetches user details by username.
     */
    public Task<QuerySnapshot> getUserByToken(String token, LoginCallback callback) {
        return DbContext.getInstance().db.collection(DbContext.getInstance().USERS_COLLECTION)
                .whereEqualTo("token", token)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        User user = userDoc.toObject(User.class);
                        callback.onLoginSuccess(user);
                    } else {
                        callback.onLoginFailed("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user by token", e);
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
    // New callback interface for registration
    public interface RegisterCallback {
        void onRegisterSuccess(User user);
        void onRegisterFailed(String errorMessage);
    }
    /**
     * Callback interface for login success or failure.
     */
    public interface LoginCallback {
        void onLoginSuccess(User user);
        void onLoginFailed(String errorMessage);
    }
}
