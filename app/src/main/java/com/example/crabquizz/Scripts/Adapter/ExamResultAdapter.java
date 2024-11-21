package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.ExamResult;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExamResultAdapter extends RecyclerView.Adapter<ExamResultAdapter.ExamResultViewHolder> {
    private List<ExamResult.StudentScore> examResults;

    public ExamResultAdapter(List<ExamResult.StudentScore> examResults) {
        this.examResults = examResults;
    }

    @NonNull
    @Override
    public ExamResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_result, parent, false);
        return new ExamResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamResultViewHolder holder, int position) {
        ExamResult.StudentScore result = examResults.get(position);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        holder.tvQuestionPack.setText("Bộ câu hỏi: " + result.getQuestionPackId());
        holder.tvScore.setText(String.format("Điểm: %.2f", result.getScore()));
        holder.tvExamDate.setText("Ngày thi: " + dateFormat.format(result.getDateDo()));
        holder.tvExamTime.setText("Thời gian: " + result.getExamTime());
        holder.tvCorrectAnswers.setText("Số câu đúng: " + result.getCorrectAnswersCount());
    }

    @Override
    public int getItemCount() {
        return examResults.size();
    }

    static class ExamResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionPack, tvScore, tvExamDate, tvExamTime, tvCorrectAnswers;

        public ExamResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionPack = itemView.findViewById(R.id.tvQuestionPack);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvExamDate = itemView.findViewById(R.id.tvExamDate);
            tvExamTime = itemView.findViewById(R.id.tvExamTime);
            tvCorrectAnswers = itemView.findViewById(R.id.tvCorrectAnswers);
        }
    }
}