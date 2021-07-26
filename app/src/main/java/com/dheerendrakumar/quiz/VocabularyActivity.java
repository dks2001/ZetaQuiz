package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class VocabularyActivity extends AppCompatActivity {
    ImageView imageView;
    Toolbar toolbar;
    private RecyclerView levelRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        imageView = findViewById(R.id.vocabicon);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VocabularyActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageView.setX(-800);
        imageView.animate().translationX(0).setDuration(500).alpha(1);

        levelRecyclerview = findViewById(R.id.levelrecyclerview);
        levelRecyclerview.setAdapter(new levelRecyclerAdapter(this));
        levelRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
}