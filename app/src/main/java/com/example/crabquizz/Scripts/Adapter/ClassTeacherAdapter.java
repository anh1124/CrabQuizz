package com.example.crabquizz.Scripts.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Controller.StudentClassController;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.example.crabquizz.Scripts.Models.StudentClass;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.StorageQuestionPackActivity;

import java.util.List;

public class ClassTeacherAdapter extends RecyclerView.Adapter<ClassTeacherAdapter.ClassViewHolder> {
    private List<StudentClass> classes;
    private DbContext dbContext;

    public ClassTeacherAdapter(List<StudentClass> classes) {
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

    private void addExam(Context context, ClassViewHolder holder, StudentClass studentClass) {
        // Tạo một Intent để mở StorageQuestionPackActivity cho việc chọn bộ câu hỏi
        Intent intent = new Intent(context, StorageQuestionPackActivity.class);

        // Đặt callback để xử lý sự kiện khi người dùng chọn một bộ câu hỏi
        StorageQuestionPackActivity.setExamCallback(new QuestionPackAdapter.OnQuestionPackClickListener() {
            @Override
            public void onQuestionPackClick(QuestionPack questionPack) {
                // Cập nhật ID bộ câu hỏi được chọn vào studentClass
                studentClass.setquestionPackIdNowForExam(questionPack.getId());

                // Cập nhật thông tin studentClass vào cơ sở dữ liệu
                dbContext.update(dbContext.CLASSES_COLLECTION, studentClass.getId(), studentClass)
                        .addOnSuccessListener(aVoid -> {
                            // Thông báo cho người dùng về kết quả thêm bài kiểm tra
                            Toast.makeText(context, "Đã thêm bài kiểm tra mới", Toast.LENGTH_SHORT).show();

                            // Cập nhật lại danh sách các lớp học trong UI
                            int position = classes.indexOf(studentClass);
                            if (position != -1) {
                                classes.set(position, studentClass);
                                notifyDataSetChanged();

                                // Hiển thị lại menu với giá trị mới của question pack
                                showPopupMenu(context, holder.menuButton, holder, studentClass, questionPack.getId());
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Thông báo lỗi nếu có sự cố khi thêm bài kiểm tra
                            Toast.makeText(context, "Lỗi khi thêm bài kiểm tra: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                            // Khôi phục lại giá trị ban đầu nếu xảy ra lỗi
                            studentClass.setquestionPackIdNowForExam("0");
                            notifyDataSetChanged();
                        });
            }
        });

        // Bắt đầu Activity để người dùng chọn bộ câu hỏi
        context.startActivity(intent);
    }



    private void stopExam(Context context, StudentClass studentClass) {
        // Cập nhật questionPackIdNowForExam về "0"
        studentClass.setquestionPackIdNowForExam("0");

        // Cập nhật lên Firestore database
        dbContext.update(dbContext.CLASSES_COLLECTION, studentClass.getId(), studentClass)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    Toast.makeText(context, "Đã dừng bài kiểm tra hiện tại", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại danh sách classes local
                    int position = classes.indexOf(studentClass);
                    if (position != -1) {
                        classes.set(position, studentClass);
                    }

                    // Cập nhật lại UI
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Cập nhật thất bại
                    Toast.makeText(context, "Lỗi khi dừng bài kiểm tra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                });
    }


    // Sửa lại phương thức onBindViewHolder để truyền holder vào addExam
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        StudentClass studentClass = classes.get(position);
        holder.className.setText(studentClass.getName());
        holder.studentCount.setText(studentClass.getStudentCount() + " học sinh");
        holder.menuButton.setOnClickListener(v -> {
            // Lấy Context và đối tượng lớp
            Context context = v.getContext();
            StudentClassController controller = new StudentClassController();

            // Gọi cập nhật questionPackId trước khi hiển thị menu
            controller.getQuestionPackIdForClass(studentClass.getId())
                    .addOnSuccessListener(questionPackId -> {
                        // Cập nhật questionPackIdNowForExam
                        studentClass.setquestionPackIdNowForExam(questionPackId);

                        // Ghi log giá trị mới
                        Log.d("ClassTeacherAdapter", "Updated questionPackIdNowForExam: " + questionPackId);

                        // Hiển thị menu sau khi cập nhật xong
                        showPopupMenu(context, v, holder, studentClass, questionPackId);
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi cập nhật thất bại
                        Log.e("ClassTeacherAdapter", "Error updating questionPackIdNowForExam", e);
                        Toast.makeText(context, "Lỗi khi lấy mã bài kiểm tra: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // Hiển thị menu ngay cả khi thất bại
                        showPopupMenu(context, v, holder, studentClass, "0");
                    });
        });
    }



    // Sửa lại phương thức showPopupMenu để kiểm tra và hiển thị đúng menu items
    private void showPopupMenu(Context context, View view, ClassViewHolder holder, StudentClass studentClass, String questionPackId) {
        PopupMenu popupMenu = new PopupMenu(context, holder.menuButton);
        popupMenu.inflate(R.menu.teacher_class_item_menu);

        // Kiểm tra trạng thái để hiển thị hoặc ẩn các mục menu
        boolean hasExam = !"0".equals(questionPackId);
        popupMenu.getMenu().findItem(R.id.menu_add_exam).setVisible(!hasExam);
        popupMenu.getMenu().findItem(R.id.menu_stop_now_exam).setVisible(hasExam);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    showDeleteConfirmationDialog(context, studentClass);
                    return true;
                case R.id.menu_copy_id:
                    copyClassIdToClipboard(context, studentClass.getId());
                    return true;
                case R.id.menu_add_exam:
                    if (!hasExam) {
                        addExam(context, holder, studentClass); // Truyền thêm holder
                        Toast.makeText(context, "chỗ này truyền đến view add chọn exam của lò thiện ", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                case R.id.menu_stop_now_exam:
                    if (hasExam) {
                        stopExam(context, studentClass);
                    }
                    return true;
                default:
                    return false;
            }
        });

        // Hiển thị menu
        popupMenu.show();
    }
    private void showDeleteConfirmationDialog(Context context, StudentClass studentClass) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lớp " + studentClass.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteClass(context, studentClass))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteClass(Context context, StudentClass studentClass) {
        // Xóa lớp từ Firestore sử dụng DbContext
        dbContext.delete(dbContext.CLASSES_COLLECTION, studentClass.getId())
                .addOnSuccessListener(aVoid -> {
                    // Xóa thành công
                    classes.remove(studentClass);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Đã xóa lớp thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xóa thất bại
                    Toast.makeText(context, "Lỗi khi xóa lớp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void copyClassIdToClipboard(Context context, String classId) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Class ID", classId);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "Đã sao chép ID lớp", Toast.LENGTH_SHORT).show();
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
    private void updateQuestionPackId(Context context, StudentClass studentClass) {
        StudentClassController controller = new StudentClassController();
        controller.getQuestionPackIdForClass(studentClass.getId())
                .addOnSuccessListener(questionPackId -> {
                    // Cập nhật giá trị questionPackIdNowForExam của class hiện tại
                    studentClass.setquestionPackIdNowForExam(questionPackId);

                    // Ghi log giá trị mới
                    Log.d("ClassTeacherAdapter", "Updated questionPackIdNowForExam: " + questionPackId);

                    // Hiển thị Toast để người dùng biết đã cập nhật thành công
                    Toast.makeText(context, "Đã cập nhật mã bài kiểm tra: " + questionPackId, Toast.LENGTH_SHORT).show();

                    // Cập nhật lại danh sách classes và UI
                    int position = classes.indexOf(studentClass);
                    if (position != -1) {
                        classes.set(position, studentClass);
                    }
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Hiển thị thông báo lỗi nếu thất bại
                    Toast.makeText(context, "Lỗi khi cập nhật bài kiểm tra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ClassTeacherAdapter", "Error updating questionPackIdNowForExam", e);
                });
    }

}