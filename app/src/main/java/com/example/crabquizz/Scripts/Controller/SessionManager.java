package com.example.crabquizz.Scripts.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.crabquizz.Scripts.Models.User;
import com.google.gson.Gson;

public class SessionManager {

    // Biến static và instance của singleton
    private static SessionManager instance;

    // Tên file SharedPreferences để lưu trữ dữ liệu
    private static final String PREF_NAME = "CrabQuizzSession";

    // Các khóa (key) để lưu trữ thông tin trong SharedPreferences
    private static final String KEY_USERNAME = "username";          // Tên đăng nhập
    private static final String KEY_FULLNAME = "fullname";          // Tên đầy đủ
    private static final String KEY_TOKEN = "token";                // Token xác thực
    private static final String KEY_USER_ROLE = "userRole";         // Vai trò người dùng (học sinh/giáo viên)
    private static final String KEY_USER_SESSION = "userSession";   // Thông tin phiên làm việc
    private static final String KEY_AUTO_LOGIN = "autoLoginEnabled"; // Trạng thái tự động đăng nhập
    private static final String KEY_TOKEN_EXPIRED_AT = "tokenExpiredAt";
    public void PrintSharedPreferencesLog() {
        // Lấy dữ liệu từ SharedPreferences
        String username = sharedPreferences.getString(KEY_USERNAME, "N/A");
        String fullName = sharedPreferences.getString(KEY_FULLNAME, "N/A");
        String token = sharedPreferences.getString(KEY_TOKEN, "N/A");
        String userRole = sharedPreferences.getString(KEY_USER_ROLE, "N/A");
        boolean autoLoginEnabled = sharedPreferences.getBoolean(KEY_AUTO_LOGIN, false);
        String tokenExpiredAt = sharedPreferences.getString(KEY_TOKEN_EXPIRED_AT, "N/A");

        // In log các thông tin lấy được
        Log.d("LOGDATA", "SharedPreferences Log:");
        Log.d("LOGDATA", "Username: " + username);
        Log.d("LOGDATA", "Full Name: " + fullName);
        Log.d("LOGDATA", "Token: " + token);
        Log.d("LOGDATA", "User Role: " + userRole);
        Log.d("LOGDATA", "Auto Login Enabled: " + autoLoginEnabled);
        Log.d("LOGDATA", "Token Expired At: " + tokenExpiredAt);
    }

    // Các biến instance để quản lý dữ liệu
    private SharedPreferences sharedPreferences;                    // Đối tượng lưu trữ dữ liệu vĩnh viễn
    private SharedPreferences.Editor editor;                        // Đối tượng chỉnh sửa SharedPreferences
    private boolean autoLoginEnabled;                               // Trạng thái tự động đăng nhập
    private UserTEMPSession userTEMPSession;                                // Thông tin phiên làm việc hiện tại

    // Lớp inner class để lưu trữ thông tin phiên làm việc tạm thời
    public static class UserTEMPSession {
        private User user;

        // Lấy thông tin người dùng trong phiên
        public User getUser() {
            return user;
        }

        // Cập nhật thông tin người dùng trong phiên
        public void setUser(User user) {
            this.user = user;
        }

        // Hàm in ra log các thông tin của người dùng hiện tại
        public void printUserSessionLog() {
            if (user != null) {
                Log.d("LOGDATA", "User TEMP Session Log:");
                Log.d("LOGDATA", "Username: " + user.getUsername());
                Log.d("LOGDATA", "Full Name: " + user.getFullName());
                Log.d("LOGDATA", "Token: " + user.getToken());
                Log.d("LOGDATA", "User Role: " + user.getRole());
            } else {
                Log.d("LOGDATA", "User session is empty. No user information available.");
            }
        }

        //hàm xóa thông tin phiên làm việc tạm thời
        public void clearSession() {
            user = null;
            user = new User();
            user.setRole("guess");
            user.setFullName("guess");
        }

        //public void newUserSession(){user = new User();}
    }

    public boolean isLogin() {
        if (userTEMPSession != null && userTEMPSession.getUser() != null) {
            String role = userTEMPSession.getUser().getRole();
            return role.equals("teacher") || role.equals("student");
        }
        return false;
    }

    public void showLogUserData()
    {
        Log.d("LOGDATA", "LOG:");
        printUserTEMPSessionLog();
        PrintSharedPreferencesLog();
    }
    //nếu lỗi khi chạy thì tại hàm này set userSession .
    public void clearUserSessionInSessionManager()
    {
        if (userTEMPSession != null) {
            userTEMPSession.clearSession();
            //userTEMPSession.newUserSession();
            Log.d("clearUserSessionInSessionManager", "has delete temp user session and make new 1");
        }
        else
        {
            Log.d("clearUserSessionInSessionManager", "temp User session is null or empty. No user information available.");
        }
    }
    public void printUserTEMPSessionLog() {
        if (userTEMPSession != null && userTEMPSession.getUser() != null) {
            userTEMPSession.printUserSessionLog();
        } else {
            Log.d("SessionManager", "User session is empty. No user information available.");
        }
    }

    // Constructor private để thực hiện Singleton pattern
    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    // Phương thức lấy instance duy nhất của SessionManager
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    //=========================================================================
    // NHÓM PHƯƠNG THỨC QUẢN LÝ SAVE
    //=========================================================================
    //lưu thông tin cơ bản của người hiện tại vừa đăng nhập
    public void SaveLoginSession(String username, String fullname, String token, String userRole, boolean autoLogin) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ROLE, userRole);
        setAutoLoginEnabled(autoLogin);
        editor.commit();
    }
    /**
     * Đăng xuất người dùng và xóa toàn bộ dữ liệu đã lưu trong sharedPreferences
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();
        clearTEMPUserAndsharedPreferencesSession();
    }
    //=========================================================================
    // NHÓM PHƯƠNG THỨC QUẢN LÝ TỰ ĐỘNG ĐĂNG NHẬP
    //=========================================================================
    public boolean isAutoLoginEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_LOGIN, false);
    }
    public void setAutoLoginEnabled(boolean enabled) {
        autoLoginEnabled = enabled;
        editor.putBoolean(KEY_AUTO_LOGIN, enabled);
        editor.commit();
    }
    //=========================================================================
    // NHÓM PHƯƠNG THỨC KIỂM TRA TRẠNG THÁI PHIÊN khi app bắt đầu
    //=========================================================================
    public boolean isHaveToken() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        if (token == null) {
            deleteSharedPreferences();
            return false;
        }
        return true;
    }
    public boolean isHaveUsername() {
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username == null) {
            deleteSharedPreferences();
            return false;
        }
        return true;
    }
    //=========================================================================
    // NHÓM PHƯƠNG THỨC LẤY THÔNG TIN CƠ BẢN trong sharedPreferences
    //=========================================================================
    public String getSharedPreferencesUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    public String getSharedPreferencesToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    public UserTEMPSession getSharedPreferencesUserDetails() {
        UserTEMPSession session = new UserTEMPSession();
        session.setUser(new User(
                sharedPreferences.getString(KEY_FULLNAME, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_USER_ROLE, null)
        ));
        return session;
    }
    //=========================================================================
    // NHÓM PHƯƠNG THỨC QUẢN LÝ PHIÊN LÀM VIỆC tạm thời
    //=========================================================================
    public void saveTEMPUserInfo(User user) {
        if (userTEMPSession == null) {
            userTEMPSession = new UserTEMPSession();
        }
        userTEMPSession.setUser(user);

        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        editor.putString(KEY_USER_SESSION, userJson);
        editor.commit();
    }
    public void saveGuessSession(User guestUser) {
        if (userTEMPSession == null) {
            userTEMPSession = new UserTEMPSession();
        }
        userTEMPSession.setUser(guestUser);
        // Note: No SharedPreferences storage for guest session, only temporary in-memory storage
    }
    public UserTEMPSession getUserSession() {
        if (userTEMPSession.user == null) {
            String userSessionData = sharedPreferences.getString(KEY_USER_SESSION, null);
            if (userSessionData != null) {
                Gson gson = new Gson();
                User user = gson.fromJson(userSessionData, User.class);

                userTEMPSession = new UserTEMPSession();
                userTEMPSession.setUser(user);
            }
        }
        return userTEMPSession;
    }
    public void clearTEMPUserAndsharedPreferencesSession() {
        userTEMPSession.clearSession();
        editor.remove(KEY_USER_SESSION);
        editor.commit();
    }
    //=========================================================================
    // PHƯƠNG THỨC TIỆN ÍCH
    //=========================================================================
    private void deleteSharedPreferences() {
        editor.clear().commit();
    }
}