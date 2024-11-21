package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.ClassScoresActivity;
import com.example.crabquizz.R;

import java.util.List;

public class ClassScoresAdapter extends RecyclerView.Adapter<ClassScoresAdapter.ScoreViewHolder> {
    private List<ClassScoresActivity.StudentScore> studentScores;

    public ClassScoresAdapter(List<ClassScoresActivity.StudentScore> studentScores) {
        this.studentScores = studentScores;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ClassScoresActivity.StudentScore studentScore = studentScores.get(position);
        holder.studentNameTextView.setText(studentScore.getName());
        holder.studentScoreTextView.setText(String.format("%.2f", studentScore.getScore()));
    }

    @Override
    public int getItemCount() {
        return studentScores.size();
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;
        TextView studentScoreTextView;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.studentNameTextView);
            studentScoreTextView = itemView.findViewById(R.id.studentScoreTextView);
        }
    }
}