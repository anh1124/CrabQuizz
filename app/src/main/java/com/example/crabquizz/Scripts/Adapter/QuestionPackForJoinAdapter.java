package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import java.util.List;

public class QuestionPackForJoinAdapter extends RecyclerView.Adapter<QuestionPackForJoinAdapter.QuestionPackForJoinViewHolder> {
    private List<Question> questions;
    private OnQuestionPackClickListener listener;

    public interface OnQuestionPackClickListener {
        void onQuestionPackClick(QuestionPack questionPack);
    }

    public QuestionPackForJoinAdapter(List<Question> questions, OnQuestionPackClickListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionPackForJoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionPackForJoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionPackForJoinViewHolder holder, int position) {
        if (questions != null && !questions.isEmpty() && position >= 0 && position < questions.size()) {
            Question currentQuestion = questions.get(position);
            holder.bind(currentQuestion);
        }
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    class QuestionPackForJoinViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvOption1, tvOption2, tvOption3, tvOption4;

        public QuestionPackForJoinViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvOption1 = itemView.findViewById(R.id.option1Text);
            tvOption2 = itemView.findViewById(R.id.option2Text);
            tvOption3 = itemView.findViewById(R.id.option3Text);
            tvOption4 = itemView.findViewById(R.id.option4Text);
        }

        public void bind(Question question) {
            tvQuestion.setText(question.getQuestion());
            tvOption1.setText(question.getAnswer1());
            tvOption2.setText(question.getAnswer2());
            tvOption3.setText(question.getAnswer3());
            tvOption4.setText(question.getAnswer4());
        }
    }

    public void updateQuestions(List<Question> newQuestions) {
        this.questions = newQuestions;
        notifyDataSetChanged();
    }
}