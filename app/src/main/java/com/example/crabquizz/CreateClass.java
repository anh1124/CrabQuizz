package com.example.crabquizz;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.StudentClassController;

public class CreateClass extends AppCompatActivity {
    private SessionManager sessonManager;
    private static final String TAG = "CreateClass";
    private EditText editTextClassName;
    private Button buttonCreateClass,buttonCancel;
    private StudentClassController studentClassController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        initPack();
        // Initialize the controller
        studentClassController = new StudentClassController();



    }

    private void initPack() {
        sessonManager = SessionManager.getInstance(this );
    }

    private void initView() {
        editTextClassName = findViewById(R.id.editTextClassName);
        buttonCreateClass = findViewById(R.id.buttonCreateClass);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Set up button click listener
        // Trong initView()
        buttonCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToStudentClassFrament();
            }
        });
    }
    public void backToStudentClassFrament(){
        //trở về fracment trước
        finish();

    }
    /**
     * Handles creating a new class by using the StudentClassController.
     */
    private void createClass() {
        String className = editTextClassName.getText().toString().trim();

        if (className.isEmpty()) {
            Toast.makeText(this, "Please enter a class name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming teacherId is hardcoded for now (replace with actual logic)
        int teacherId = sessonManager.getInstance(this).getUserSession().getUser().getId();
        Log.d(TAG, "Class created: " + className);
        studentClassController.checkAndCreateClass(teacherId, className, (success, message) -> {
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(CreateClass.this, "Class created successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Class created: " + className);
                    backToStudentClassFrament();
                } else {
                    Toast.makeText(CreateClass.this, message, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to create class: " + message);
                }
            });
        });
    }

}