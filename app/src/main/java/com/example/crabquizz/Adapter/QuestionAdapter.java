package com.example.crabquizz.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.Question;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private ArrayList<Question> questions;

    // Constructor
    public QuestionAdapter(ArrayList<Question> questions) {
        this.questions = questions;
    }

    // Tạo view holder cho item
    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.example.crabquizz.R.layout.item_question_form, parent, false);
        return new QuestionViewHolder(view);
    }

    // Gán dữ liệu cho các view trong item
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.questionText.setText(question.getQuestion());
        holder.optionA.setText(question.getAnswer1());
        holder.optionB.setText(question.getAnswer2());
        holder.optionC.setText(question.getAnswer3());
        holder.optionD.setText(question.getAnswer4());
    }

    // Số lượng item trong RecyclerView
    @Override
    public int getItemCount() {
        return questions.size();
    }

    // ViewHolder cho từng câu hỏi
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, optionA, optionB, optionC, optionD;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionNumber);
            optionA = itemView.findViewById(R.id.optionAInput);
            optionB = itemView.findViewById(R.id.optionBInput);
            optionC = itemView.findViewById(R.id.optionCInput);
            optionD = itemView.findViewById(R.id.optionDInput);
        }
    }
}
