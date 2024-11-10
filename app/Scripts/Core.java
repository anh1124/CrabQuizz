package app.Script.Core;


import android.content.Context;
import android.content.SharedPreferences;


public class Core {
    private static Core instance;


    private String username;
    private String password;
    private String token;

    // Private constructor để tránh khởi tạo từ bên ngoài
    private Core() {
    }

    // Phương thức truy cập duy nhất cho instance của lớp Core
    public static synchronized Core getInstance() {
        if (instance == null) {
            instance = new Core();
        }
        return instance;
    }

    // Các phương thức khác của Core
    public void initialize() {
        // Logic khởi tạo
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Lấy username, password, và token từ SharedPreferences
        this.username = sharedPreferences.getString("username", null);
        this.password = sharedPreferences.getString("password", null);
        this.token = sharedPreferences.getString("token", null);
    }


    // Getter và Setter cho username, password, token
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

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    // Phương thức kiểm tra username, password và token
    public boolean checkCredentials(String inputUsername, String inputPassword, String inputToken) {
        return inputUsername.equals(this.username) &&
                inputPassword.equals(this.password) &&
                inputToken.equals(this.token);
    }

    // Phương thức kiểm tra username, password và token
    public boolean checkCredentials(String inputUsername, String inputPassword, String inputToken) {
        // Kiểm tra nếu username, password, hoặc token chưa được khởi tạo (null)
        if (this.username == null || this.password == null || this.token == null) {
            return false;
        }

        // So sánh các thông tin đã lưu với thông tin đầu vào
        return inputUsername.equals(this.username) &&
                inputPassword.equals(this.password) &&
                inputToken.equals(this.token);
    }
    // Phương thức xóa username, password và token khỏi SharedPreferences
    public void clearCredentials(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("token");
        editor.apply();

        // Xóa các giá trị đã lưu trong instance
        this.username = null;
        this.password = null;
        this.token = null;
    }
}
