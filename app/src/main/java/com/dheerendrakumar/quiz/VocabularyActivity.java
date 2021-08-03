package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class VocabularyActivity extends AppCompatActivity {
    ImageView imageView;
    Toolbar toolbar;
    private RecyclerView levelRecyclerview;
    ArrayList<String> scores = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        imageView = findViewById(R.id.vocabicon);
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();

        ArrayList<String> scores = getIntent().getStringArrayListExtra("scores");

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
        levelRecyclerview.setAdapter(new levelRecyclerAdapter(VocabularyActivity.this,scores));
        levelRecyclerview.setLayoutManager(new LinearLayoutManager(VocabularyActivity.this));


    }
}