package com.backdoorz.phishshieldai;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class VisitedUrlAdapter extends RecyclerView.Adapter<VisitedUrlAdapter.UrlViewHolder> {
    private List<VisitedUrl> urls = new ArrayList<>();

    @NonNull
    @Override
    public UrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visited_url, parent, false);
        return new UrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlViewHolder holder, int position) {
        VisitedUrl url = urls.get(position);
        holder.bind(url);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public void setUrls(List<VisitedUrl> newUrls) {
        urls = newUrls;
        notifyDataSetChanged();
    }

    static class UrlViewHolder extends RecyclerView.ViewHolder {
        private final TextView urlText;
        private final TextView scoreText;
        private final TextView dateText;

        public UrlViewHolder(@NonNull View itemView) {
            super(itemView);
            urlText = itemView.findViewById(R.id.urlText);
            scoreText = itemView.findViewById(R.id.scoreText);
            dateText = itemView.findViewById(R.id.dateText);
        }

        public void bind(VisitedUrl url) {
            urlText.setText(url.getUrl());
            dateText.setText(url.getDate());

            // Set status text
            scoreText.setText(url.getStatus());

            // Set status color based on status
            int colorResId;
            switch (url.getStatus().toLowerCase()) {
                case "safe":
                    colorResId = R.color.status_safe;
                    break;
                case "suspicious":
                    colorResId = R.color.status_suspicious;
                    break;
                case "dangerous":
                    colorResId = R.color.status_dangerous;
                    break;
                default:
                    colorResId = R.color.primary;
                    break;
            }

            int color = ContextCompat.getColor(itemView.getContext(), colorResId);
            scoreText.getBackground().setTint(color);
        }
    }
}
