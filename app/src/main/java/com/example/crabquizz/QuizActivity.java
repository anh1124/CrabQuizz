package com.example.crabquizz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crabquizz.Scripts.Adapter.QuestionPackForJoinAdapter;
import com.example.crabquizz.Scripts.Controller.SessionManager;
import com.example.crabquizz.Scripts.Models.DbContext;
import com.example.crabquizz.Scripts.Models.ExamResult;
import com.example.crabquizz.Scripts.Models.Question;
import com.example.crabquizz.Scripts.Models.QuestionAnswer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuizActivity extends AppCompatActivity implements QuestionPackForJoinAdapter.OnQuestionPackClickListener {
    private static final int TOTAL_TIME_MINUTES = 25;
    private static final String DATE_FORMAT = "dd/MM/yyyy, hh:mm:ss a";

    private ImageButton backButton;
    private Button btnComplete, btnNext, btnPrev;
    private TextView tvTimer;
    private RecyclerView recyclerViewQuestions;

    private List<Question> questions = new ArrayList<>();
    private QuestionPackForJoinAdapter adapter;

    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private String dateDo;
    private DbContext dbContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeComponents();
        setupQuizFlow();
    }

    private void initializeComponents() {
        dateDo = getCurrentDate();
        dbContext = DbContext.getInstance();

        backButton = findViewById(R.id.BackButton);
        recyclerViewQuestions = findViewById(R.id.recyclerViewQuestionsAnswer);
        btnComplete = findViewById(R.id.btnComplete);
        tvTimer = findViewById(R.id.tvTimer);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        adapter = new QuestionPackForJoinAdapter(questions, this);
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewQuestions.setAdapter(adapter);

        setupClickListeners();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        btnComplete.setOnClickListener(v -> saveExamResultToDatabase());
        btnPrev.setOnClickListener(v -> navigateQuestion(-1));
        btnNext.setOnClickListener(v -> navigateQuestion(1));
    }

    private void setupQuizFlow() {
        loadQuestions();
        startTimer();
    }

    private void loadQuestions() {
        String packQuestionJson = getIntent().getStringExtra("packQuestionJson");
        List<Question> questionList = new Gson().fromJson(packQuestionJson, new TypeToken<List<Question>>(){}.getType());

        if (questionList != null && !questionList.isEmpty()) {
            questions.addAll(questionList);
            adapter.updateQuestions(questions);
            updateQuestionDisplay();
        } else {
            Toast.makeText(this, "No questions in this pack", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startTimer() {
        timeLeftInMillis = TimeUnit.MINUTES.toMillis(TOTAL_TIME_MINUTES);
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                stopQuiz();
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void stopQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Toast.makeText(this, "Time's up! Please submit your quiz.", Toast.LENGTH_SHORT).show();
    }

    private void navigateQuestion(int direction) {
        if (direction < 0 && currentQuestionIndex > 0) {
            currentQuestionIndex--;
        } else if (direction > 0 && currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
        } else {
            Toast.makeText(this, direction < 0 ? "First question" : "Last question", Toast.LENGTH_SHORT).show();
            return;
        }

        updateQuestionDisplay();
        restoreSelectedOption();
    }

    private void restoreSelectedOption() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        if (currentQuestion.getSelectedOption() != -1) {
            adapter.setSelectedOption(currentQuestion.getSelectedOption());
        }
    }

    private void updateQuestionDisplay() {
        List<Question> currentQuestion = new ArrayList<>();
        currentQuestion.add(questions.get(currentQuestionIndex));
        adapter.updateQuestions(currentQuestion);
        recyclerViewQuestions.scrollToPosition(0);

        adapter.notifyItemChanged(0);
    }

    @Override
    public void onOptionSelected(int optionIndex) {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            currentQuestion.setSelectedOption(optionIndex);

            QuestionAnswer questionAnswer = new QuestionAnswer(
                    currentQuestion.getId(),
                    optionIndex
            );

            dbContext.add("questionanswers", questionAnswer)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Answer saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show());

            if (optionIndex == currentQuestion.getCorrectAnswer()) {
                correctAnswersCount++;
            }
            adapter.setSelectedOption(optionIndex);
        }
    }

    private void saveExamResultToDatabase() {
        int studentID = SessionManager.getInstance(this).getUserSession().getUser().getId();
        String packQuestionID = getIntent().getStringExtra("packId");
        int score = calculateScore();

        long timeUsed = calculateTimeUsed();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeUsed);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeUsed) % 60;
        String formattedTimeUsed = String.format("%02d:%02d", minutes, seconds);

        ExamResult.StudentScore studentScore = new ExamResult.StudentScore(
                studentID,
                dateDo,
                score,
                packQuestionID,
                String.valueOf(correctAnswersCount),
                formattedTimeUsed
        );
            dbContext.add("studenscores", studentScore)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Exam result saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show());
            openExamScreen(studentScore);
    }

    private void openExamScreen(ExamResult.StudentScore studentScore) {
        String packTitle = getIntent().getStringExtra("packTitle");
        Log.d("dskdsadsk", "score = " + packTitle);
        int score = calculateScore();

        Intent intent = new Intent(QuizActivity.this, ExamResultActivity.class);
        intent.putExtra("correctAnswer", studentScore.getCorrectAnswersCount());
        intent.putExtra("examtime", studentScore.getExamTime());
        intent.putExtra("score", String.valueOf(score));
        intent.putExtra("quesionPackTitle", packTitle);
        startActivity(intent);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
    }

    private long calculateTimeUsed() {
        return TimeUnit.MINUTES.toMillis(TOTAL_TIME_MINUTES) - timeLeftInMillis;
    }

    private int calculateScore() {
        return Math.round((float) correctAnswersCount / questions.size() * 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}