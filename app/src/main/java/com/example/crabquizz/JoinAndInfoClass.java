package com.example.crabquizz;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.StudentClassController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.User;

public class JoinAndInfoClass extends AppCompatActivity {
    private EditText editTextID, editTextClassName, editTextStudentCount;
    private Button buttonBack, buttonJoin;
    private DbContext dbContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_and_info_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initPackage();
        initView();

        // Lấy dữ liệu từ Intent
        String classId = getIntent().getStringExtra("classId");
        String className = getIntent().getStringExtra("className");
        int studentCount = getIntent().getIntExtra("studentCount", 0);

        // Hiển thị dữ liệu lên các EditText
        if (classId != null) editTextID.setText(classId);
        if (className != null) editTextClassName.setText(className);
        editTextStudentCount.setText(String.valueOf(studentCount));
    }

    private void initPackage() {
        dbContext = DbContext.getInstance();
    }

    private void initView() {
        // Ánh xạ EditText
        editTextID = findViewById(R.id.editTextText3);
        editTextClassName = findViewById(R.id.editTextText);
        editTextStudentCount = findViewById(R.id.editTextText2);

        // Ánh xạ Button
        buttonBack = findViewById(R.id.buttonBack);
        buttonJoin = findViewById(R.id.buttonJoin);

        buttonJoin.setOnClickListener(v -> {
            String classId = editTextID.getText().toString();
            User user = SessionManager.getInstance(this).getUserSession().getUser();

            if (classId.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã lớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add student to class
            StudentClassController controller = new StudentClassController();
            controller.addStudentToClass(classId, user.getId())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm học sinh với ID " + user.getId() + " vào lớp: " + classId + " thành công!", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after adding the student
                    })
                    .addOnFailureListener(e -> {
                        Log.e("JoinAndInfoClass", "Error adding student: ", e);
                        Toast.makeText(this, "Đã xảy ra lỗi khi thêm học sinh vào lớp.", Toast.LENGTH_SHORT).show();
                    });
        });

        buttonBack.setOnClickListener(v -> {
            finish(); // Quay lại màn hình trước đó
        });


    }

}