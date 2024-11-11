package com.example.crabquizz.Scripts;

import android.content.Context;
import android.content.SharedPreferences;

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

    // Các biến instance để quản lý dữ liệu
    private SharedPreferences sharedPreferences;                    // Đối tượng lưu trữ dữ liệu vĩnh viễn
    private SharedPreferences.Editor editor;                        // Đối tượng chỉnh sửa SharedPreferences
    private boolean autoLoginEnabled;                               // Trạng thái tự động đăng nhập
    private UserSession userSession;                                // Thông tin phiên làm việc hiện tại

    // Lớp inner class để lưu trữ thông tin phiên làm việc tạm thời
    public static class UserSession {
        private User user;

        // Lấy thông tin người dùng trong phiên
        public User getUser() {
            return user;
        }

        // Cập nhật thông tin người dùng trong phiên
        public void setUser(User user) {
            this.user = user;
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

    /**
     * Tạo phiên đăng nhập mới cho người dùng
     * @param username  Tên đăng nhập
     * @param fullname  Tên đầy đủ
     * @param token     Token xác thực
     * @param userRole  Vai trò người dùng
     * @param autoLogin Bật/tắt tự động đăng nhập
     */
    //lưu thông tin cơ bản của người hiện tại vừa đăng nhập
    public void createLoginSession(String username, String fullname, String token, String userRole, boolean autoLogin) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ROLE, userRole);
        setAutoLoginEnabled(autoLogin);
        editor.commit();
    }

    /**
     * Đăng xuất người dùng và xóa toàn bộ dữ liệu đã lưu
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    //=========================================================================
    // NHÓM PHƯƠNG THỨC QUẢN LÝ TỰ ĐỘNG ĐĂNG NHẬP
    //=========================================================================

    /**
     * Kiểm tra trạng thái tự động đăng nhập
     * @return true nếu tự động đăng nhập được bật
     */
    public boolean isAutoLoginEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_LOGIN, false);
    }

    /**
     * Cập nhật trạng thái tự động đăng nhập
     * @param enabled true để bật tự động đăng nhập
     */
    public void setAutoLoginEnabled(boolean enabled) {
        autoLoginEnabled = enabled;
        editor.putBoolean(KEY_AUTO_LOGIN, enabled);
        editor.commit();
    }

    //=========================================================================
    // NHÓM PHƯƠNG THỨC KIỂM TRA TRẠNG THÁI PHIÊN khi app bắt đầu
    //=========================================================================

    /**
     * Kiểm tra token xác thực còn tồn tại hay không
     * @return true nếu token còn tồn tại
     */
    public boolean isHaveToken() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        if (token == null) {
            deleteSharedPreferences();
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra username còn tồn tại hay không
     * @return true nếu username còn tồn tại
     */
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

    /**
     * Lấy tên đăng nhập của người dùng
     * @return tên đăng nhập hoặc null nếu chưa đăng nhập
     */
    public String getSharedPreferencesUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    /**
     * Lấy token xác thực của người dùng
     * @return token hoặc null nếu chưa đăng nhập
     */
    public String getSharedPreferencesToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * Lấy toàn bộ thông tin chi tiết của người dùng
     * @return đối tượng UserSession chứa thông tin người dùng
     */
    public UserSession getSharedPreferencesUserDetails() {
        UserSession session = new UserSession();
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

    /**
     * Lưu thông tin người dùng vào phiên làm việc
     * @param user đối tượng User chứa thông tin cần lưu
     */
    public void saveUserInfo(User user) {
        if (userSession == null) {
            userSession = new UserSession();
        }

        userSession.setUser(user);

        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        editor.putString(KEY_USER_SESSION, userJson);
        editor.commit();
    }

    public void saveGuessSession(User guestUser) {
        if (userSession == null) {
            userSession = new UserSession();
        }
        userSession.setUser(guestUser);
        // Note: No SharedPreferences storage for guest session, only temporary in-memory storage
    }

    /**
     * Lấy thông tin phiên làm việc hiện tại
     * @return đối tượng UserSession chứa thông tin phiên
     */
    public UserSession getUserSession() {
        if (userSession.user == null) {
            String userSessionData = sharedPreferences.getString(KEY_USER_SESSION, null);
            if (userSessionData != null) {
                Gson gson = new Gson();
                User user = gson.fromJson(userSessionData, User.class);

                userSession = new UserSession();
                userSession.setUser(user);
            }
        }
        return userSession;
    }

    /**
     * Xóa thông tin phiên làm việc hiện tại
     */
    public void clearUserSession() {
        userSession = null;
        editor.remove(KEY_USER_SESSION);
        editor.commit();
    }

    //=========================================================================
    // PHƯƠNG THỨC TIỆN ÍCH
    //=========================================================================

    /**
     * Xóa toàn bộ dữ liệu trong SharedPreferences
     */
    private void deleteSharedPreferences() {
        editor.clear().commit();
    }
}