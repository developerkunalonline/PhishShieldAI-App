package com.backdoorz.phishshieldai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.google.android.material.button.MaterialButton;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuizQuestion> questions;
    private List<Integer> selectedAnswers;
    private AnswerSelectedListener answerSelectedListener;

    public interface AnswerSelectedListener {
        void onAnswerSelected(int position);
    }

    public QuizAdapter(List<QuizQuestion> questions, List<Integer> selectedAnswers, AnswerSelectedListener listener) {
        this.questions = questions;
        this.selectedAnswers = selectedAnswers;
        this.answerSelectedListener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_question, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizQuestion question = questions.get(position);
        holder.bind(question, position, selectedAnswers.get(position));

        // Set radio group listener
        holder.optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < holder.options.length; i++) {
                if (holder.options[i].getId() == checkedId) {
                    selectedAnswers.set(holder.getAdapterPosition(), i);
                    if (answerSelectedListener != null) {
                        answerSelectedListener.onAnswerSelected(holder.getAdapterPosition());
                    }
                    break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public int getSelectedAnswerAt(int position) {
        return selectedAnswers.get(position);
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionText;
        final RadioGroup optionsGroup;
        private final RadioButton[] options;
        private final MaterialButton explainButton;
        private final TextView explanationText;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            optionsGroup = itemView.findViewById(R.id.optionsGroup);
            options = new RadioButton[]{
                itemView.findViewById(R.id.option1),
                itemView.findViewById(R.id.option2),
                itemView.findViewById(R.id.option3),
                itemView.findViewById(R.id.option4)
            };
            explainButton = itemView.findViewById(R.id.explainButton);
            explanationText = itemView.findViewById(R.id.explanationText);
        }

        public void bind(QuizQuestion question, int position, int selectedAnswerIndex) {
            // Set question text with number
            questionText.setText(String.format("Q%d. %s", position + 1, question.getQuestion()));

            // Set options
            List<String> optionsList = question.getOptions();
            for (int i = 0; i < Math.min(options.length, optionsList.size()); i++) {
                options[i].setText(optionsList.get(i));
                options[i].setVisibility(View.VISIBLE);
            }

            // Restore selected answer
            optionsGroup.setOnCheckedChangeListener(null); // Prevent listener firing during setup
            if (selectedAnswerIndex != -1 && selectedAnswerIndex < options.length) {
                options[selectedAnswerIndex].setChecked(true);
            } else {
                optionsGroup.clearCheck();
            }

            // Setup explanation button
            explainButton.setOnClickListener(v -> {
                if (explanationText.getVisibility() == View.GONE) {
                    explanationText.setText(question.getExplanation());
                    explanationText.setVisibility(View.VISIBLE);
                    explainButton.setText("Hide Explanation");
                } else {
                    explanationText.setVisibility(View.GONE);
                    explainButton.setText("Explain");
                }
            });
        }
    }
}
