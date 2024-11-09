package com.example.crabquizz;

import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
public class MainActivity extends AppCompatActivity {
    private TextView adminDataTextView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Tìm TextView
        adminDataTextView = findViewById(R.id.adminDataTextView);

        // Thiết lập padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Đọc dữ liệu từ collection "admins"
        loadAdminData();
    }

    private void loadAdminData() {
        db.collection("admins")
                .document("pDPl8tOemTkPhw66ckKC")  // ID của document trong ảnh của bạn
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy dữ liệu từ document
                        String email = documentSnapshot.getString("email");
                        String fullName = documentSnapshot.getString("fullName");
                        String username = documentSnapshot.getString("username");

                        // Tạo chuỗi hiển thị
                        String displayText = "Admin Information:\n" +
                                "Email: " + email + "\n" +
                                "Full Name: " + fullName + "\n" +
                                "Username: " + username;

                        // Hiển thị lên TextView
                        adminDataTextView.setText(displayText);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error loading admin data", e);
                    adminDataTextView.setText("Error loading data: " + e.getMessage());
                });
    }
}