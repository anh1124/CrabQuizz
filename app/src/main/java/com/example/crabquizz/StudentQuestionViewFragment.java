package com.example.crabquizz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.Scripts.Adapter.QuestionPackAdapter;
import com.example.crabquizz.Scripts.Controller.NavigationController;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.QuestionPack;

import java.util.ArrayList;
import java.util.List;

public class StudentQuestionViewFragment extends Fragment implements QuestionPackAdapter.OnQuestionPackClickListener {
    private RecyclerView questionViewRecycler;
    private QuestionPackAdapter adapter;
    private List<QuestionPack> questionPacks;
    private DbContext dbContext;
    private NavigationController navigationController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_question_view, container, false);

        questionViewRecycler = view.findViewById(R.id.questionsViewRecycler);
        dbContext = DbContext.getInstance();
        questionPacks = new ArrayList<>();
        navigationController = new NavigationController(requireActivity());

        setUpRecycler();
        loadQuestionPacks();

        return view;
    }

    private void setUpRecycler() {
        adapter = new QuestionPackAdapter(questionPacks, this);
        questionViewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        questionViewRecycler.setAdapter(adapter);
    }

    private void loadQuestionPacks() {
        dbContext.getAll("questionpacks")
                .addOnSuccessListener(querySnapshots -> {
                    questionPacks.clear();
                    List<QuestionPack> packs = dbContext.convertToList(querySnapshots, QuestionPack.class);
                    questionPacks.addAll(packs);
                    adapter.updateData(questionPacks);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khi tải bộ câu hỏi", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onQuestionPackClick(QuestionPack questionPack) {
        // Lấy thông tin cần thiết từ questionPack
        String questionId = questionPack.getId(); // Giả sử có phương thức getQuestionId() trong QuestionPack
        String questionText = questionPack.getTitle(); // Giả sử có phương thức getQuestionText() trong QuestionPack

        // Tạo một instance mới của DetailsQuestionFragment
        DetailsQuestionFragment detailsQuestionFragment = DetailsQuestionFragment.newInstance(questionId, questionText);

        // Chuyển đến fragment DetailsQuestionFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsQuestionFragment) // Đảm bảo R.id.fragment_container là ID của container chứa các fragment
                .addToBackStack(null) // Cho phép quay lại fragment trước đó
                .commit();
    }

}