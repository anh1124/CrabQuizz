package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class QuestionPackAdapter extends RecyclerView.Adapter<QuestionPackAdapter.QuestionPackViewHolder> {
    private List<QuestionPack> questionPacks;
    private OnQuestionPackClickListener listener;

    public interface OnQuestionPackClickListener {
        void onQuestionPackClick(QuestionPack questionPack);
    }

    public QuestionPackAdapter(List<QuestionPack> questionPacks, OnQuestionPackClickListener listener) {
        this.questionPacks = questionPacks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionPackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_pack, parent, false);
        return new QuestionPackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionPackViewHolder holder, int position) {
        QuestionPack questionPack = questionPacks.get(position);
        holder.bind(questionPack);
    }

    @Override
    public int getItemCount() {
        return questionPacks.size();
    }

    class QuestionPackViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTopic;
        MaterialCardView cardQuestionPack;

        public QuestionPackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvQuestionPackTitle);
            tvDescription = itemView.findViewById(R.id.tvQuestionPackDescription);
            tvTopic = itemView.findViewById(R.id.tvQuestionPackTopic);
            cardQuestionPack = itemView.findViewById(R.id.cardQuestionPack);
        }

        public void bind(QuestionPack questionPack) {
            tvTitle.setText(questionPack.getTitle());
            tvDescription.setText(questionPack.getDescription());
            tvTopic.setText(questionPack.getTopic());

            cardQuestionPack.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuestionPackClick(questionPack);
                }
            });
        }
    }

    public void updateData(List<QuestionPack> newPacks) {
        this.questionPacks = newPacks;
        notifyDataSetChanged();
    }
}