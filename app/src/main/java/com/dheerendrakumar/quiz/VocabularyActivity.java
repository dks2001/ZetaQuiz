package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;


public class VocabularyActivity extends AppCompatActivity {
    private RecyclerView levelRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        levelRecyclerview = findViewById(R.id.levelrecyclerview);
        levelRecyclerview.setAdapter(new levelRecyclerAdapter(this));
        levelRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
}