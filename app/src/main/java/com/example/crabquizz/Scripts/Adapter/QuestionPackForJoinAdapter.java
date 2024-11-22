package com.example.crabquizz.Scripts.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private OnQuestionPackClickListener listener;
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
        if (questions != null && !questions.isEmpty() && position >= 0 && position < questions.size()) {
            Question currentQuestion = questions.get(position);
            holder.bind(currentQuestion, selectedOptionIndex, position, questions.size());
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
        TextView tvQuestion, tvOption1, tvOption2, tvOption3, tvOption4, tvStepProgress;
        MaterialCardView option1Card, option2Card, option3Card, option4Card;
        OnQuestionPackClickListener listener;

        public QuestionPackForJoinViewHolder(@NonNull View itemView, OnQuestionPackClickListener listener) {
            super(itemView);
            this.listener = listener;
            Context context = itemView.getContext();

            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvOption1 = itemView.findViewById(R.id.option1Text);
            tvOption2 = itemView.findViewById(R.id.option2Text);
            tvOption3 = itemView.findViewById(R.id.option3Text);
            tvOption4 = itemView.findViewById(R.id.option4Text);
            tvStepProgress = itemView.findViewById(R.id.tvStepProgress);

            option1Card = itemView.findViewById(R.id.option1Card);
            option2Card = itemView.findViewById(R.id.option2Card);
            option3Card = itemView.findViewById(R.id.option3Card);
            option4Card = itemView.findViewById(R.id.option4Card);

            // Set up click listeners
            option1Card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionSelected(1);
                }
            });

            option2Card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionSelected(2);
                }
            });

            option3Card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionSelected(3);
                }
            });

            option4Card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionSelected(4);
                }
            });
        }

        public void bind(Question question, int selectedOptionIndex, int currentIndex, int totalQuestions) {
            tvQuestion.setText(question.getQuestion());
            tvOption1.setText(question.getAnswer1());
            tvOption2.setText(question.getAnswer2());
            tvOption3.setText(question.getAnswer3());
            tvOption4.setText(question.getAnswer4());

            // Cập nhật tiến trình
            tvStepProgress.setText(String.format("Step %d/%d", currentIndex + 1, totalQuestions));

            // Reset màu
            resetCardColors();

            // Đánh dấu màu cho option đã chọn
            if (selectedOptionIndex != -1) {
                MaterialCardView selectedCard = getCardByIndex(selectedOptionIndex);
                if (selectedCard != null) {
                    selectedCard.setCardBackgroundColor(
                            ContextCompat.getColor(itemView.getContext(), R.color.green)
                    );
                }
            }
        }


        private void resetCardColors() {
            option1Card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.background));
            option2Card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.background));
            option3Card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.background));
            option4Card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.background));
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