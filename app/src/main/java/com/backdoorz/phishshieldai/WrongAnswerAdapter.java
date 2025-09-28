package com.backdoorz.phishshieldai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WrongAnswerAdapter extends RecyclerView.Adapter<WrongAnswerAdapter.WrongAnswerViewHolder> {

    static class WrongAnswer {
        String question;
        String yourAnswer;
        String correctAnswer;
        String explanation;

        WrongAnswer(String question, String yourAnswer, String correctAnswer, String explanation) {
            this.question = question;
            this.yourAnswer = yourAnswer;
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
        }
    }

    private List<WrongAnswer> wrongAnswers;

    public WrongAnswerAdapter(List<WrongAnswer> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    @NonNull
    @Override
    public WrongAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wrong_answer, parent, false);
        return new WrongAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WrongAnswerViewHolder holder, int position) {
        WrongAnswer wrongAnswer = wrongAnswers.get(position);
        holder.bind(wrongAnswer);
    }

    @Override
    public int getItemCount() {
        return wrongAnswers.size();
    }

    static class WrongAnswerViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionText;
        private final TextView yourAnswerText;
        private final TextView correctAnswerText;
        private final TextView explanationText;

        public WrongAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            yourAnswerText = itemView.findViewById(R.id.yourAnswerText);
            correctAnswerText = itemView.findViewById(R.id.correctAnswerText);
            explanationText = itemView.findViewById(R.id.explanationText);
        }

        public void bind(WrongAnswer wrongAnswer) {
            questionText.setText(wrongAnswer.question);
            yourAnswerText.setText(wrongAnswer.yourAnswer);
            correctAnswerText.setText(wrongAnswer.correctAnswer);
            explanationText.setText(wrongAnswer.explanation);
        }
    }
}
