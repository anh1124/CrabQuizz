package com.example.crabquizz.Scripts.Controller;

import android.util.Log;

import com.example.crabquizz.Scripts.Models.User;
import com.example.crabquizz.Scripts.Models.AppSetup;

import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.TokenGen;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

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


    //sử dụng cho việc tạo id mới
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
    //sử dụng cho việc tạo tài khoản với 1 email duy nhất (emailacuatoi)
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


    // Phương thức đăng ký người dùng mới
    public void register(String fullName, String username, String password, String role, String email, Callback callback) {
        // Kiểm tra xem username đã tồn tại trong database hay chưa
        dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Nếu tìm thấy kết quả (tức là username đã tồn tại)
                    if (!querySnapshot.isEmpty()) {
                        // Gọi hàm callback báo lỗi đăng ký vì username đã tồn tại
                        callback.onFailed("Username already exists");
                        return;
                    }
                    // Nếu username chưa tồn tại, kiểm tra email
                    isEmailUnique(email).addOnSuccessListener(isUnique -> {
                        if (!isUnique) {
                            // Nếu email không duy nhất, gọi callback báo lỗi
                            callback.onFailed("Email already exists");
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
                                        callback.onSuccess(newUser);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Nếu có lỗi khi thêm vào database, ghi log lỗi và gọi callback báo lỗi
                                        Log.e(TAG, "Registration error", e);
                                        callback.onFailed(e.getMessage());
                                    });
                        }).addOnFailureListener(e -> {
                            // Nếu có lỗi khi lấy ID người dùng cao nhất, ghi log lỗi và gọi callback báo lỗi
                            Log.e(TAG, "Error getting highest user ID", e);
                            callback.onFailed(e.getMessage());
                        });

                    }).addOnFailureListener(e -> {
                        // Nếu có lỗi khi kiểm tra email trong database, ghi log lỗi và gọi callback báo lỗi
                        Log.e(TAG, "Email check error", e);
                        callback.onFailed(e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    // Nếu có lỗi khi kiểm tra username trong database, ghi log lỗi và gọi callback báo lỗi
                    Log.e(TAG, "Username check error", e);
                    callback.onFailed(e.getMessage());
                });
    }

    //hàm này được tạo ra bởi 1 cách thần kỳ do db không lấy được token nên phải làm hàm này nên đừng xóa:((
    public Task<String> getRoleByToken(String token) {
        return dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("token", token)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                        User user = userDoc.toObject(User.class);
                        if (user != null && user.getRole() != null) {
                            return user.getRole().toString(); // Trả về role dưới dạng String nếu tìm thấy
                        } else {
                            Log.e(TAG, "Role not found or is null.");
                            return null; // Trả về null nếu role không tìm thấy hoặc null
                        }
                    } else {
                        Log.e(TAG, "User not found with given token.");
                        return null; // Trả về null nếu không tìm thấy user với token
                    }
                });
    }

    //đăng nhập bằng username password
    public void loginWithUsernameAndPassword(String username, String password, Callback callback) {
        dbContext.db.collection(dbContext.getUsersCollection())
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        User user = userDoc.toObject(User.class);

                        // Kiểm tra xem dữ liệu User đã bao gồm thuộc tính role chưa
                        if (user != null) {

                            // Set token mới và cập nhật tokenExpiredAt
                            String newToken = TokenGen.getInstance().getRandomToken();
                            user.setToken(newToken);

                            // Tính ngày hết hạn của token
                            int maxValidityDays = AppSetup.getInstance().getMaxTokenValidityDays();
                            Date newExpirationDate = new Date(System.currentTimeMillis() + maxValidityDays * 24L * 60L * 60L * 1000L);
                            user.setTokenExpiredAt(newExpirationDate);

                            // Cập nhật token và ngày hết hạn trong cơ sở dữ liệu
                            userDoc.getReference().update("token", newToken, "tokenExpiredAt", newExpirationDate)
                                    .addOnSuccessListener(aVoid -> {
                                        sessionManager.saveTEMPUserInfo(user);
                                        callback.onSuccess(user);
                                        Log.d(TAG, "User logged in successfully with role: " + user.getRole());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Token update error", e);
                                        callback.onFailed(e.getMessage());
                                    });
                        } else {
                            callback.onFailed("Failed to convert document to User object.");
                        }
                    } else {
                        callback.onFailed("Invalid username or password.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login error", e);
                    callback.onFailed(e.getMessage());
                });
    }
    //phương thức đăng nhâp dử dụng khi mở app nếu autologin true
    public void loginWithUsernameAndToken(String username, String token, Callback callback) {
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
                                            sessionManager.saveTEMPUserInfo(user);
                                            callback.onSuccess(user);
                                            Log.d(TAG, "User logged in successfully");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Token update error", e);
                                            callback.onFailed(e.getMessage());
                                        });
                            } else {
                                sessionManager.saveTEMPUserInfo(user);
                                callback.onSuccess(user);
                                Log.d(TAG, "User logged in successfully");
                            }
                        } else {
                            callback.onFailed("Failed to convert document to User object");
                        }
                    } else {
                        callback.onFailed("Invalid username or token");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login error", e);
                    callback.onFailed(e.getMessage());
                });
    }

    public Task<QuerySnapshot> getUserByToken(String token, Callback callback) {
        return DbContext.getInstance().db.collection(DbContext.getInstance().USERS_COLLECTION)
                .whereEqualTo("token", token)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        User user = userDoc.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailed("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user by token", e);
                    callback.onFailed(e.getMessage());
                });
    }

    //hàm này sẽ xóa các thông tin trong SharedPreferences
    public void logout(CallLogOut callLogOut) {
        //được thêm interface để nhắc cua cua sẽ nhớ thêm cái GoLoginMenu vào đây
        sessionManager.logoutUser();
        Log.d("logout", "User logged out successfully");
        if (callLogOut != null) {
            callLogOut.GoLoginMenu();
        }
    }

    public interface Callback {
        void onSuccess(User user);
        void onFailed(String errorMessage);
    }
    public interface CallLogOut{
        void GoLoginMenu();
    }
}
