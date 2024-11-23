package com.example.crabquizz;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsQuestionFragment extends Fragment {

    // Khởi tạo các tham số cho fragment
    private static final String ARG_QUESTION_ID = "question_id";
    private static final String ARG_QUESTION_TEXT = "question_text";

    // Các tham số của fragment
    private String questionId;
    private String questionText;

    public DetailsQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method để tạo một instance của fragment với các tham số cần thiết.
     *
     * @param questionId ID câu hỏi.
     * @param questionText Nội dung câu hỏi.
     * @return Một instance mới của fragment DetailsQuestionFragment.
     */
    public static DetailsQuestionFragment newInstance(String questionId, String questionText) {
        DetailsQuestionFragment fragment = new DetailsQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_ID, questionId);
        args.putString(ARG_QUESTION_TEXT, questionText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionId = getArguments().getString(ARG_QUESTION_ID);
            questionText = getArguments().getString(ARG_QUESTION_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout cho fragment
        View rootView = inflater.inflate(R.layout.fragment_details_question, container, false);

        // Cập nhật UI nếu cần dựa trên tham số
        // Ví dụ: hiển thị câu hỏi hoặc thực hiện các hành động khác với questionId và questionText

        return rootView;
    }
}
