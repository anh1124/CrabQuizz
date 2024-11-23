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

public class ExamResultAdapter extends RecyclerView.Adapter<ExamResultAdapter.ViewHolder> {
    private List<ExamResult.StudentScore> examResults;

    public ExamResultAdapter(List<ExamResult.StudentScore> examResults) {
        this.examResults = examResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamResult.StudentScore score = examResults.get(position);

        holder.tvDate.setText("Ngày thi: " + score.getDateDo());
        holder.tvScore.setText(String.format("Điểm: %.1f", score.getScore()));
        holder.tvCorrectAnswers.setText("Số câu đúng: " + score.getCorrectAnswersCount());
        holder.tvExamTime.setText("Thời gian làm bài: " + score.getExamTime());
    }

    @Override
    public int getItemCount() {
        return examResults != null ? examResults.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvScore, tvCorrectAnswers, tvExamTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvCorrectAnswers = itemView.findViewById(R.id.tvCorrectAnswers);
            tvExamTime = itemView.findViewById(R.id.tvExamTime);
        }
    }
}