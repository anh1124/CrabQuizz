package com.example.crabquizz.Scripts.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.QuizActivity;
import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Controller.ExamResultController;
import com.example.crabquizz.Scripts.Controller.QuestionPackController;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.StudentClassController;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.example.crabquizz.Scripts.Models.StudentClass;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.StudentExamResultsActivity;

import java.util.List;

public class ClassStudentAdapter extends RecyclerView.Adapter<ClassStudentAdapter.ClassViewHolder> {
    private List<StudentClass> classes;
    private DbContext dbContext;

    public ClassStudentAdapter(List<StudentClass> classes) {
        this.classes = classes;
        this.dbContext = DbContext.getInstance();
    }

    public void updateData(List<StudentClass> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        StudentClass studentClass = classes.get(position);
        holder.className.setText(studentClass.getName());
        holder.studentCount.setText(studentClass.getStudentCount() + " học sinh");

        holder.menuButton.setOnClickListener(v -> {
            Context context = v.getContext();

            // Here, add logic for fetching any relevant data, such as grades or current exams, if needed.

            showPopupMenu(context, v, holder, studentClass);
        });
    }

    private void showPopupMenu(Context context, View view, ClassViewHolder holder, StudentClass studentClass) {
        PopupMenu popupMenu = new PopupMenu(context, holder.menuButton);
        popupMenu.inflate(R.menu.student_class_item_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_exit_class:
                    exitClass(context, studentClass);
                    return true;
                case R.id.menu_view_grades:
                    viewGrades(context, studentClass);
                    return true;
                case R.id.menu_take_exam:
                    takeExam(context, studentClass);
                    return true;
                default:
                    return false;
            }
        });

        // Show the menu
        popupMenu.show();
    }

    private void exitClass(Context context, StudentClass studentClass) {
        int currentStudentId = SessionManager.getInstance(context).getUserSession().getUser().getId();
        StudentClassController classController = new StudentClassController();

        classController.exitClassForStudent(studentClass.getId(), currentStudentId)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Bạn đã thoát lớp " + studentClass.getName(), Toast.LENGTH_SHORT).show();
                    // Remove this class from the RecyclerView
                    int position = classes.indexOf(studentClass);
                    if (position != -1) {
                        classes.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Không thể thoát lớp. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                });
    }

    private void viewGrades(Context context, StudentClass studentClass) {
        int studentId = SessionManager.getInstance(context).getUserSession().getUser().getId(); // Student ID
        String classId = studentClass.getId(); // Class ID

        // Create an Intent to launch the StudentExamResultsActivity
        Intent intent = new Intent(context, StudentExamResultsActivity.class);
        intent.putExtra(StudentExamResultsActivity.EXTRA_STUDENT_ID, studentId);
        intent.putExtra(StudentExamResultsActivity.EXTRA_CLASS_ID, classId);
        context.startActivity(intent);
    }

    private void takeExam(Context context, StudentClass studentClass) {
        QuestionPackController controller = new QuestionPackController();
        String questionPackId = studentClass.getquestionPackIdNowForExam();
        int studentId = SessionManager.getInstance(context).getUserSession().getUser().getId();

        Log.d("TakeExamm", "Starting takeExam with questionPackId: " + questionPackId);

        // Kiểm tra xem học sinh đã làm bài kiểm tra này chưa
        ExamResultController examResultController = new ExamResultController();
        examResultController.getStudentScoresInClass(studentId, studentClass.getId())
                .addOnSuccessListener(scores -> {
                    // Kiểm tra xem có kết quả nào với questionPackId hiện tại không
                    boolean hasAttempted = scores.stream()
                            .anyMatch(score -> score.getQuestionPackId().equals(questionPackId));

                    if (hasAttempted) {
                        // Học sinh đã làm bài kiểm tra này
                        Toast.makeText(context,
                                "Bạn đã làm bài kiểm tra này rồi!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Nếu chưa làm, tiếp tục với logic hiện tại
                    controller.getQuestionPackById(questionPackId)
                            .addOnSuccessListener(questionPack -> {
                                Log.d("TakeExamm", "getQuestionPackById success");
                                String questionJson = questionPack.getQuestionJson();
                                Log.d("TakeExamm", "questionJson: " +
                                        (questionJson != null ? questionJson.substring(0, Math.min(100, questionJson.length())) + "..." : "null"));

                                if (questionJson != null) {
                                    Intent intent = new Intent(context, QuizActivity.class);
                                    intent.putExtra("classId", studentClass.getId());
                                    intent.putExtra("packId", questionPackId);
                                    intent.putExtra("packQuestionJson", questionJson);
                                    intent.putExtra("packTitle", studentClass.getName());

                                    Log.d("TakeExamm", "Starting QuizActivity with:");
                                    Log.d("TakeExamm", "packId: " + questionPackId);
                                    Log.d("TakeExamm", "packTitle: " + studentClass.getName());
                                    Log.d("TakeExamm", "packTitle: " + studentClass.getId());

                                    context.startActivity(intent);

                                    Toast.makeText(context,
                                            "Bắt đầu làm bài kiểm tra của lớp " + studentClass.getName(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("TakeExam", "questionJson is null");
                                    Toast.makeText(context,
                                            "Bài kiểm tra không có nội dung",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("TakeExamm", "Failed to get QuestionPack", e);
                                Toast.makeText(context,
                                        "Không thể tải bài kiểm tra: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("TakeExamm", "Failed to check exam results", e);
                    Toast.makeText(context,
                            "Không thể kiểm tra kết quả bài kiểm tra: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public int getItemCount() {
        return classes.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView studentCount;
        ImageButton menuButton;

        public ClassViewHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            studentCount = itemView.findViewById(R.id.studentCount);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
    }
}
