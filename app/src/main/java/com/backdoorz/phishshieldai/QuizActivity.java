package com.backdoorz.phishshieldai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
    private List<QuizQuestion> questions;
    private List<Integer> selectedAnswers;
    private QuizAdapter quizAdapter;
    private ProgressBar loadingProgressBar;
    private RecyclerView questionsRecyclerView;
    private MaterialButton submitButton;
    private PrefsManager prefsManager;
    private static final String QUIZ_URL = "https://phishshield-backend-15e6.onrender.com/quiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        prefsManager = new PrefsManager(this);

        // Initialize views
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        submitButton = findViewById(R.id.submitButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        questions = new ArrayList<>();
        selectedAnswers = new ArrayList<>();

        // Initially hide RecyclerView and submit button, show loading
        questionsRecyclerView.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        // Setup RecyclerView
        quizAdapter = new QuizAdapter(questions, selectedAnswers, this::updateAnswer);
        questionsRecyclerView.setAdapter(quizAdapter);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            if (selectedAnswers.contains(-1)) {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
                return;
            }
            checkAnswers();
        });

        // Fetch questions from API
        fetchQuestions();
    }

    private void updateAnswer(int position) {
        selectedAnswers.set(position, quizAdapter.getSelectedAnswerAt(position));
    }

    private void fetchQuestions() {
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            QUIZ_URL,
            null,
            response -> {
                try {
                    // Clear any existing questions
                    questions.clear();
                    selectedAnswers.clear();

                    // Iterate through the questions in the response
                    for (int i = 0; response.has(String.valueOf(i)); i++) {
                        JSONObject questionObj = response.getJSONObject(String.valueOf(i));
                        String questionText = questionObj.getString("question");
                        org.json.JSONArray optionsArray = questionObj.getJSONArray("options");
                        String correctAnswer = questionObj.getString("answer");
                        String explanation = questionObj.getString("explanation");

                        // Convert options array to List
                        List<String> options = new ArrayList<>();
                        for (int j = 0; j < optionsArray.length(); j++) {
                            options.add(optionsArray.getString(j));
                        }

                        // Find correct answer index
                        int correctIndex = options.indexOf(correctAnswer);

                        // Add question
                        questions.add(new QuizQuestion(questionText, options, correctIndex, explanation));
                        selectedAnswers.add(-1);
                    }

                    // Create new adapter with updated data
                    quizAdapter = new QuizAdapter(questions, selectedAnswers, this::updateAnswer);
                    questionsRecyclerView.setAdapter(quizAdapter);

                    // Show questions and hide loading
                    loadingProgressBar.setVisibility(View.GONE);
                    questionsRecyclerView.setVisibility(View.VISIBLE);
                    submitButton.setVisibility(View.VISIBLE);

                    Toast.makeText(this, "Quiz loaded successfully", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    handleError("Error parsing response: " + e.getMessage());
                }
            },
            error -> handleError("Network error: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = prefsManager.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Add request to queue
        Volley.newRequestQueue(this).add(request);
    }

    private void handleError(String message) {
        loadingProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void checkAnswers() {
        int correctAnswers = 0;
        ArrayList<String> wrongQuestions = new ArrayList<>();
        ArrayList<String> wrongAnswers = new ArrayList<>();
        ArrayList<String> correctAnswersList = new ArrayList<>();
        ArrayList<String> explanationsList = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion question = questions.get(i);
            int selectedAnswer = selectedAnswers.get(i);

            if (selectedAnswer == question.getCorrectOptionIndex()) {
                correctAnswers++;
            } else {
                wrongQuestions.add(question.getQuestion());
                wrongAnswers.add(question.getOptions().get(selectedAnswer));
                correctAnswersList.add(question.getOptions().get(question.getCorrectOptionIndex()));
                explanationsList.add(question.getExplanation());
            }
        }

        // Launch ResultActivity with the results
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_SCORE, correctAnswers);
        intent.putExtra(ResultActivity.EXTRA_TOTAL, questions.size());
        intent.putStringArrayListExtra(ResultActivity.EXTRA_WRONG_QUESTIONS, wrongQuestions);
        intent.putStringArrayListExtra(ResultActivity.EXTRA_WRONG_ANSWERS, wrongAnswers);
        intent.putStringArrayListExtra(ResultActivity.EXTRA_CORRECT_ANSWERS, correctAnswersList);
        intent.putStringArrayListExtra(ResultActivity.EXTRA_EXPLANATIONS, explanationsList);
        startActivity(intent);
        finish();
    }
}
