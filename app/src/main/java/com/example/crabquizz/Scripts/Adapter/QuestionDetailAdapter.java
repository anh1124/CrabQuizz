package com.example.crabquizz.Scripts.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.R;
import com.example.crabquizz.Scripts.Models.Question;

import java.util.List;

public class QuestionDetailAdapter extends RecyclerView.Adapter<QuestionDetailAdapter.QuestionViewHolder> {

    // Danh sách câu hỏi
    private List<Question> questions;

    // Constructor để truyền danh sách câu hỏi
    public QuestionDetailAdapter(List<Question> questions) {
        this.questions = questions;
    }

    // Tạo ViewHolder mới cho mỗi item
    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item cho câu hỏi
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    // Liên kết dữ liệu với item trong RecyclerView
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        // Lấy câu hỏi từ danh sách
        Question question = questions.get(position);
        // Gọi hàm bind() để gắn dữ liệu vào view
        holder.bind(question);
    }

    // Lấy số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return questions != null ? questions.size() : 0;
    }

    // ViewHolder để chứa các view của mỗi item
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        // Khai báo TextView để hiển thị câu hỏi
        private TextView textViewQuestion;

        // Constructor để khởi tạo các view trong item
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Lấy TextView từ layout item_question.xml
            textViewQuestion = itemView.findViewById(R.id.textViewQuestion);
        }

        // Bind dữ liệu cho item
        public void bind(Question question) {
            // Gắn dữ liệu vào TextView
            textViewQuestion.setText(question.getQuestion());
        }
    }
}
