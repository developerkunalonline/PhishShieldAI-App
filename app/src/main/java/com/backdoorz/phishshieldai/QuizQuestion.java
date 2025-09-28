package com.backdoorz.phishshieldai;

import java.util.List;

public class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctOptionIndex;
    private String explanation;
    private boolean isExplanationVisible;

    public QuizQuestion(String question, List<String> options, int correctOptionIndex, String explanation) {
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
        this.isExplanationVisible = false;
    }

    // Getters
    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public boolean isExplanationVisible() {
        return isExplanationVisible;
    }

    public void setExplanationVisible(boolean visible) {
        isExplanationVisible = visible;
    }
}
