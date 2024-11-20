package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.Scripts.Adapter.QuestionPackAdapter;
import com.example.crabquizz.Scripts.Controller.MenuNavigationClickController;
import com.example.crabquizz.Scripts.Controller.NavigationController;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Controller.TransitionFragemt;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.QuestionPack;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends Fragment implements QuestionPackAdapter.OnQuestionPackClickListener {
    private RecyclerView recyclerView;
    private QuestionPackAdapter adapter;
    private List<QuestionPack> questionPacks;
    private ExtendedFloatingActionButton createQuestionPackButton;
    private DbContext dbContext;
    private NavigationController navigationController;
    private Button buttonStore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbContext = DbContext.getInstance();
        navigationController = new NavigationController(requireActivity());
        questionPacks = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);

        initializeViews(rootView);
        setupRecyclerView();
        setupClickListeners();
        //loadQuestionPacks();
        TransitionFragemt.initializeMenuNavigation(requireContext(), getParentFragmentManager(), rootView);
        Transiton();

        return rootView;
    }

    private void initializeViews(View rootView) {
        recyclerView = rootView.findViewById(R.id.questionsRecyclerView);
        createQuestionPackButton = rootView.findViewById(R.id.fabCreateQuestion);
        buttonStore = rootView.findViewById(R.id.buttonStore);
    }

    private void setupRecyclerView() {
        adapter = new QuestionPackAdapter(questionPacks, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        createQuestionPackButton.setOnClickListener(view -> {
            String role = SessionManager.getInstance(requireContext())
                    .getUserSession()
                    .getUser()
                    .getRole();

            switch (role) {
                case "teacher":
                    QuestionPackCreateFragment fragment = new QuestionPackCreateFragment();
                    navigationController.navigateTo(fragment);
                    break;
                case "student":
                    Toast.makeText(requireContext(),
                            "Chỉ giáo viên mới thể tạo câu hỏi",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(requireContext(),
                            "Đăng nhập để tiếp tục",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

//    private void loadQuestionPacks() {
//        DbContext.getInstance()
//                .getAll("questionpacks") // Giả sử collection name là "questionpacks"
//                .addOnSuccessListener(querySnapshots -> {
//                    questionPacks.clear();
//                    List<QuestionPack> packs = DbContext.getInstance()
//                            .convertToList(querySnapshots, QuestionPack.class);
//                    questionPacks.addAll(packs);
//                    adapter.updateData(questionPacks);
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Error loading question packs", Toast.LENGTH_SHORT).show();
//                });
//    }

    @Override
    public void onQuestionPackClick(QuestionPack questionPack) {
        // Navigate to quiz or question detail fragment
        QuizFragment quizFragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString("packId", questionPack.getId());
        quizFragment.setArguments(args);
        navigationController.navigateTo(quizFragment);
    }
    public void Transiton(){
        buttonStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), StorageQuestionPackActivity.class);
                startActivity(intent);
            }
        });
    }
}