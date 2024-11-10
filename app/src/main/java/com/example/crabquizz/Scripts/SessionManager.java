package com.example.crabquizz.Scripts;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.crabquizz.Scripts.Models.User;
import com.google.gson.Gson;

public class SessionManager {
    // Singleton instance
    private static SessionManager instance;

    // SharedPreferences để lưu dữ liệu phiên đăng nhập
    //SharedPreferences (Lưu trữ vĩnh viễn)
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Tên file SharedPreferences
    private static final String PREF_NAME = "CrabQuizzSession";

    // Các khóa (key) để lưu trữ trạng thái và thông tin người dùng
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";  // Để kiểm tra người dùng có đăng nhập không
    private static final String KEY_USERNAME = "username";        // Lưu tên đăng nhập
    private static final String KEY_FULLNAME = "fullname";        // Lưu tên đầy đủ của người dùng
    private static final String KEY_TOKEN = "token";              // Lưu mã token của người dùng
    private static final String KEY_USER_ROLE = "userRole";       //student or teacher
    private static final String KEY_USER_SESSION = "userSession"; // Thêm khóa để lưu trữ thông tin UserSession
    //không lưu password

    private UserSession userSession;



    // Constructor của SessionManager, dùng private để giới hạn truy cập từ bên ngoài lớp này
    private SessionManager(Context context) {
        // Khởi tạo SharedPreferences với tên tệp PREF_NAME ở chế độ PRIVATE
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Tạo đối tượng Editor để chỉnh sửa dữ liệu SharedPreferences
        editor = sharedPreferences.edit();
    }

    // Phương thức để lấy instance (singleton pattern)
    public static synchronized SessionManager getInstance(Context context) {
        // Nếu instance chưa tồn tại, tạo mới
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }


    // Phương thức lưu phiên đăng nhập của người dùng
    public void createLoginSession(String username, String fullname, String token, String userRole) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);       // Đánh dấu trạng thái đăng nhập là true
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ROLE, userRole);
        editor.commit();                                 // Lưu lại tất cả thay đổi
    }
    public boolean isHaveToken() {
        // Lấy token từ SharedPreferences
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        if (token == null) {
            // Nếu token không tồn tại, xóa toàn bộ SharedPreferences
            deleteSharedPreferences();
            return false;
        }
        return true;
    }
    public boolean isHaveUsername()
    {
        // Lấy username từ SharedPreferences
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username == null) {
            // Nếu username không tồn tại, xóa toàn bộ SharedPreferences
            deleteSharedPreferences();
            return false;
        }
        return true;
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    // Phương thức xóa toàn bộ SharedPreferences
    private void deleteSharedPreferences() {
        // Xóa tất cả dữ liệu trong SharedPreferences
        editor.clear().commit();
    }



    // Phương thức kiểm tra xem người dùng có đăng nhập hay chưa
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false); // Trả về true nếu đăng nhập, ngược lại false
    }

    // Phương thức lấy thông tin phiên đăng nhập của người dùng
    public UserSession getUserDetails() {
        UserSession session = new UserSession();
        session.setUser(new User(
                sharedPreferences.getString(KEY_FULLNAME, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_USER_ROLE, null)
        ));
        return session;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    // Phương thức lưu thông tin người dùng vào UserSession
    public void saveUserInfo(User user) {
        if (userSession == null) {
            userSession = new UserSession();
        }

        userSession.setUser(user);

        // Sử dụng Gson để chuyển đổi đối tượng User thành chuỗi JSON
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        editor.putString(KEY_USER_SESSION, userJson);  // Lưu chuỗi JSON vào SharedPreferences
        editor.commit();
    }

    // Lấy UserSession từ SharedPreferences
    public UserSession getUserSession() {
        if (userSession == null) {
            String userSessionData = sharedPreferences.getString(KEY_USER_SESSION, null);
            if (userSessionData != null) {
                // Chuyển chuỗi JSON trở lại đối tượng User bằng Gson
                Gson gson = new Gson();
                User user = gson.fromJson(userSessionData, User.class);

                userSession = new UserSession();
                userSession.setUser(user);
            }
        }
        return userSession;
    }

    //UserSession (Lưu trữ tạm thời trong bộ nhớ)
    public static class UserSession {
        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

    }

    public void clearUserSession() {
        // Set userSession to null
        userSession = null;

        // Remove the userSession data from SharedPreferences
        editor.remove(KEY_USER_SESSION);
        editor.commit();
    }

}