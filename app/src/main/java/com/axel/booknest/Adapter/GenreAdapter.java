package com.axel.booknest.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.axel.booknest.R;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private List<String> genres;
    private OnGenreClickListener onGenreClickListener;
    private int selectedPosition = 0;  // Track the selected genre for UI highlighting

    public GenreAdapter(List<String> genres, OnGenreClickListener onGenreClickListener) {
        this.genres = genres;
        this.onGenreClickListener = onGenreClickListener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_item, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        String genre = genres.get(position);
        holder.genreTextView.setText(genre);

        // Highlight the selected genre
        if (selectedPosition == position) {
            holder.genreTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            holder.genreTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            onGenreClickListener.onGenreClick(genre);  // Notify the activity
            notifyDataSetChanged(); // Refresh to apply highlight
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView genreTextView;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreTextView = itemView.findViewById(R.id.genreTextView);
        }
    }

    public interface OnGenreClickListener {
        void onGenreClick(String genre);
    }
}

