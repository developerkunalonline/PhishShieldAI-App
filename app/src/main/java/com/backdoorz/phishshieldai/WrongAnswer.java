package com.backdoorz.phishshieldai;

public class WrongAnswer {
    private String question;
    private String yourAnswer;
    private String correctAnswer;
    private String explanation;

    public WrongAnswer(String question, String yourAnswer, String correctAnswer, String explanation) {
        this.question = question;
        this.yourAnswer = yourAnswer;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public String getQuestion() {
        return question;
    }

    public String getYourAnswer() {
        return yourAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }
}
