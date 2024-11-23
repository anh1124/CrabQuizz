package com.example.crabquizz.Scripts.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.Question;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class QuestionPackForJoinAdapter extends RecyclerView.Adapter<QuestionPackForJoinAdapter.QuestionPackForJoinViewHolder> {
    private List<Question> questions;
    private final OnQuestionPackClickListener listener;
    private int selectedOptionIndex = -1;

    public interface OnQuestionPackClickListener {
        void onOptionSelected(int position);
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
        return new QuestionPackForJoinViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionPackForJoinViewHolder holder, int position) {
        if (questions != null && position < questions.size()) {
            holder.bind(questions.get(position), selectedOptionIndex, position, questions.size());
        }
    }

    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    public void updateQuestions(List<Question> newQuestions) {
        this.questions = newQuestions;
        this.selectedOptionIndex = -1;
        notifyDataSetChanged();
    }

    public void setSelectedOption(int optionIndex) {
        this.selectedOptionIndex = optionIndex;
        notifyDataSetChanged();
    }

    static class QuestionPackForJoinViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion, tvOption1, tvOption2, tvOption3, tvOption4, tvStepProgress;
        private final ProgressBar progressBar;
        private final MaterialCardView option1Card, option2Card, option3Card, option4Card;
        private final OnQuestionPackClickListener listener;

        public QuestionPackForJoinViewHolder(@NonNull View itemView, OnQuestionPackClickListener listener) {
            super(itemView);
            this.listener = listener;
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvOption1 = itemView.findViewById(R.id.option1Text);
            tvOption2 = itemView.findViewById(R.id.option2Text);
            tvOption3 = itemView.findViewById(R.id.option3Text);
            tvOption4 = itemView.findViewById(R.id.option4Text);
            tvStepProgress = itemView.findViewById(R.id.tvStepProgress);
            progressBar = itemView.findViewById(R.id.progressBar);
            option1Card = itemView.findViewById(R.id.option1Card);
            option2Card = itemView.findViewById(R.id.option2Card);
            option3Card = itemView.findViewById(R.id.option3Card);
            option4Card = itemView.findViewById(R.id.option4Card);
            setCardListeners();
        }

        private void setCardListeners() {
            option1Card.setOnClickListener(v -> listener.onOptionSelected(1));
            option2Card.setOnClickListener(v -> listener.onOptionSelected(2));
            option3Card.setOnClickListener(v -> listener.onOptionSelected(3));
            option4Card.setOnClickListener(v -> listener.onOptionSelected(4));
        }

        public void bind(Question question, int selectedOptionIndex, int currentIndex, int totalQuestions) {
            tvQuestion.setText(question.getQuestion());
            tvOption1.setText(question.getAnswer1());
            tvOption2.setText(question.getAnswer2());
            tvOption3.setText(question.getAnswer3());
            tvOption4.setText(question.getAnswer4());
            tvStepProgress.setText(String.format(" %d of %d", currentIndex + 1, totalQuestions));
            progressBar.setProgress((int) (((float) (currentIndex + 1) / totalQuestions) * 100));
            resetCardColors();
            highlightSelectedOption(selectedOptionIndex);
        }

        private void resetCardColors() {
            int defaultColor = ContextCompat.getColor(itemView.getContext(), R.color.background);
            option1Card.setCardBackgroundColor(defaultColor);
            option2Card.setCardBackgroundColor(defaultColor);
            option3Card.setCardBackgroundColor(defaultColor);
            option4Card.setCardBackgroundColor(defaultColor);
        }

        private void highlightSelectedOption(int index) {
            MaterialCardView selectedCard = getCardByIndex(index);
            if (selectedCard != null) {
                selectedCard.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
            }
        }

        private MaterialCardView getCardByIndex(int index) {
            switch (index) {
                case 1: return option1Card;
                case 2: return option2Card;
                case 3: return option3Card;
                case 4: return option4Card;
                default: return null;
            }
        }
    }
}
