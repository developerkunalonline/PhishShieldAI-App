package com.backdoorz.phishshieldai;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extra_score";
    public static final String EXTRA_TOTAL = "extra_total";
    public static final String EXTRA_WRONG_QUESTIONS = "extra_wrong_questions";
    public static final String EXTRA_WRONG_ANSWERS = "extra_wrong_answers";
    public static final String EXTRA_CORRECT_ANSWERS = "extra_correct_answers";
    public static final String EXTRA_EXPLANATIONS = "extra_explanations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get score details from intent
        int score = getIntent().getIntExtra(EXTRA_SCORE, 0);
        int total = getIntent().getIntExtra(EXTRA_TOTAL, 0);
        ArrayList<String> wrongQuestions = getIntent().getStringArrayListExtra(EXTRA_WRONG_QUESTIONS);
        ArrayList<String> wrongAnswers = getIntent().getStringArrayListExtra(EXTRA_WRONG_ANSWERS);
        ArrayList<String> correctAnswers = getIntent().getStringArrayListExtra(EXTRA_CORRECT_ANSWERS);
        ArrayList<String> explanations = getIntent().getStringArrayListExtra(EXTRA_EXPLANATIONS);

        // Initialize views
        TextView scoreText = findViewById(R.id.scoreText);
        TextView congratsText = findViewById(R.id.congratsText);
        TextView reviewTitle = findViewById(R.id.reviewTitle);
        RecyclerView wrongAnswersRecyclerView = findViewById(R.id.wrongAnswersRecyclerView);
        MaterialButton finishButton = findViewById(R.id.finishButton);

        // Set score text
        scoreText.setText(String.format("Your Score: %d/%d", score, total));

        // Set congrats message based on score
        String congratsMessage;
        if (score == total) {
            congratsMessage = "Perfect! You're a phishing expert! 🌟";
        } else if (score >= total * 0.8) {
            congratsMessage = "Great job! Keep up the good work! 🎉";
        } else if (score >= total * 0.6) {
            congratsMessage = "Good effort! Room for improvement! 💪";
        } else {
            congratsMessage = "Keep learning! You'll get better! 📚";
        }
        congratsText.setText(congratsMessage);

        // Setup wrong answers list if any wrong answers exist
        if (wrongQuestions != null && !wrongQuestions.isEmpty()) {
            List<WrongAnswerAdapter.WrongAnswer> wrongAnswersList = new ArrayList<>();
            for (int i = 0; i < wrongQuestions.size(); i++) {
                wrongAnswersList.add(new WrongAnswerAdapter.WrongAnswer(
                    wrongQuestions.get(i),
                    wrongAnswers.get(i),
                    correctAnswers.get(i),
                    explanations.get(i)
                ));
            }

            WrongAnswerAdapter adapter = new WrongAnswerAdapter(wrongAnswersList);
            wrongAnswersRecyclerView.setAdapter(adapter);
            wrongAnswersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            // Hide review section if no wrong answers
            reviewTitle.setVisibility(View.GONE);
            wrongAnswersRecyclerView.setVisibility(View.GONE);
        }

        // Handle finish button click
        finishButton.setOnClickListener(v -> finish());
    }
}
