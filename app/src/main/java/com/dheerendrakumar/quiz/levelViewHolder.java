package com.dheerendrakumar.quiz;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class levelViewHolder extends RecyclerView.ViewHolder {

    private TextView scoretextView;
    private Button levelButton;

    public levelViewHolder(@NonNull View itemView) {
        super(itemView);

        scoretextView = itemView.findViewById(R.id.scorevocab);
        levelButton = itemView.findViewById(R.id.levelButton);
    }

    public TextView getScoretextView() {
        return scoretextView;
    }

    public Button getLevelButton() {
        return levelButton;
    }
}
