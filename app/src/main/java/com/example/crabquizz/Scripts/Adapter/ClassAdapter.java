package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.StudentClass;

import java.util.List;
public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<StudentClass> classes;

    public ClassAdapter(List<StudentClass> classes) {
        this.classes = classes;
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
        // Bạn có thể cập nhật thêm các thông tin khác của lớp học ở đây nếu cần
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
