package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.Question;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private ArrayList<Question> questions;
    private OnQuestionInteractionListener listener;
    private QuestionInteractionListener editListener;

    // Interface gốc để xử lý các sự kiện từ item
    public interface OnQuestionInteractionListener {
        void onDeleteQuestion(int position);
        void onQuestionChanged(int position, String questionText);
        void onAnswerChanged(int position, int answerIndex, String answerText);
        void onCorrectAnswerChanged(int position, int correctAnswer);
    }

    // Interface mới cho chỉnh sửa câu hỏi
    public interface QuestionInteractionListener {
        void onQuestionEdit(Question question);
        void onQuestionDelete(Question question);
    }

    // Constructor với listener mới
    public QuestionAdapter(ArrayList<Question> questions, QuestionInteractionListener editListener) {
        this.questions = questions;
        this.editListener = editListener;
    }

    // Constructor với listener cũ
    public QuestionAdapter(ArrayList<Question> questions, OnQuestionInteractionListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_form, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);

        // Set question number
        holder.questionNumber.setText("Câu hỏi " + (position + 1));

        // Set existing data
        holder.questionInput.setText(question.getQuestion());
        holder.optionAInput.setText(question.getAnswer1());
        holder.optionBInput.setText(question.getAnswer2());
        holder.optionCInput.setText(question.getAnswer3());
        holder.optionDInput.setText(question.getAnswer4());

        // Set correct answer
        switch (question.getCorrectAnswer()) {
            case 1:
                holder.optionARadio.setChecked(true);
                break;
            case 2:
                holder.optionBRadio.setChecked(true);
                break;
            case 3:
                holder.optionCRadio.setChecked(true);
                break;
            case 4:
                holder.optionDRadio.setChecked(true);
                break;
        }

        // Set listeners for delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onQuestionDelete(question);
            } else if (listener != null) {
                listener.onDeleteQuestion(position);
            }
        });

        // Set click listener for entire item để chỉnh sửa
        holder.itemView.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onQuestionEdit(question);
            }
        });

        // Các listeners cho nhập liệu nếu dùng OnQuestionInteractionListener
        if (listener != null) {
            setupDetailedListeners(holder, position);
        }
    }

    private void setupDetailedListeners(QuestionViewHolder holder, int position) {
        holder.questionInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                listener.onQuestionChanged(position, holder.questionInput.getText().toString());
            }
        });

        holder.optionAInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                listener.onAnswerChanged(position, 0, holder.optionAInput.getText().toString());
            }
        });

        holder.optionBInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                listener.onAnswerChanged(position, 1, holder.optionBInput.getText().toString());
            }
        });

        holder.optionCInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                listener.onAnswerChanged(position, 2, holder.optionCInput.getText().toString());
            }
        });

        holder.optionDInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                listener.onAnswerChanged(position, 3, holder.optionDInput.getText().toString());
            }
        });

        holder.answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.optionARadio) {
                listener.onCorrectAnswerChanged(position, 1);
            } else if (checkedId == R.id.optionBRadio) {
                listener.onCorrectAnswerChanged(position, 2);
            } else if (checkedId == R.id.optionCRadio) {
                listener.onCorrectAnswerChanged(position, 3);
            } else if (checkedId == R.id.optionDRadio) {
                listener.onCorrectAnswerChanged(position, 4);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    // Method to update questions list
    public void updateQuestions(ArrayList<Question> newQuestions) {
        this.questions = newQuestions;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumber;
        EditText questionInput;
        EditText optionAInput, optionBInput, optionCInput, optionDInput;
        RadioButton optionARadio, optionBRadio, optionCRadio, optionDRadio;
        RadioGroup answerGroup;
        ImageButton deleteButton;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            // Initialize views
            questionNumber = itemView.findViewById(R.id.questionNumber);
            questionInput = itemView.findViewById(R.id.questionInput);
            optionAInput = itemView.findViewById(R.id.optionAInput);
            optionBInput = itemView.findViewById(R.id.optionBInput);
            optionCInput = itemView.findViewById(R.id.optionCInput);
            optionDInput = itemView.findViewById(R.id.optionDInput);
            optionARadio = itemView.findViewById(R.id.optionARadio);
            optionBRadio = itemView.findViewById(R.id.optionBRadio);
            optionCRadio = itemView.findViewById(R.id.optionCRadio);
            optionDRadio = itemView.findViewById(R.id.optionDRadio);
            answerGroup = itemView.findViewById(R.id.correctAnswerGroup);
            deleteButton = itemView.findViewById(R.id.deleteQuestionButton);
        }
    }
}