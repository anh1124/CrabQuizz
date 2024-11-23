package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.ExamResult;

import java.util.List;

public class ClassScoresAdapter extends RecyclerView.Adapter<ClassScoresAdapter.ViewHolder> {
    private List<ExamResult.StudentScore> scores;

    public ClassScoresAdapter(List<ExamResult.StudentScore> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_score_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamResult.StudentScore score = scores.get(position);

        holder.tvStudentId.setText("Học sinh ID: " + score.getStudentId());
        holder.tvDate.setText("Ngày thi: " + score.getDateDo());
        holder.tvScore.setText(String.format("Điểm: %.1f", score.getScore()));
        holder.tvCorrectAnswers.setText("Số câu đúng: " + score.getCorrectAnswersCount());
    }

    @Override
    public int getItemCount() {
        return scores != null ? scores.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentId, tvDate, tvScore, tvCorrectAnswers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvCorrectAnswers = itemView.findViewById(R.id.tvCorrectAnswers);
        }
    }
}