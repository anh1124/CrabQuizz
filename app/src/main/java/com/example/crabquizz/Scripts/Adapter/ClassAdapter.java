package com.example.crabquizz.Scripts.Adapter;

import android.content.Context;
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
import com.example.crabquizz.Scripts.Models.StudentClass;
import com.example.crabquizz.Scripts.Models.DbContext;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<StudentClass> classes;
    private DbContext dbContext;

    public ClassAdapter(List<StudentClass> classes) {
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
            PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.menuButton);
            popupMenu.inflate(R.menu.class_item_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        showDeleteConfirmationDialog(v.getContext(), studentClass);
                        return true;
                    case R.id.menu_copy_id:
                        copyClassIdToClipboard(v.getContext(), studentClass.getId());
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });
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
}