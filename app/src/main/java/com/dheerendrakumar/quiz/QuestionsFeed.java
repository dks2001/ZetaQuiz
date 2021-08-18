package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class QuestionsFeed extends AppCompatActivity {

    FirebaseFirestore db;
    LinearLayout questionsLinearLayout;

    ArrayList<String> imageUrl = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> question = new ArrayList<>();
    ArrayList<String> likedQuestions = new ArrayList<>();
    ArrayList<String> numberOfLikes = new ArrayList<>();
    ArrayList<String> numberOfComments = new ArrayList<>();
    String imageUrll="";
   // ProgressDialog progress;

    String userName="";
   // FirebaseAuth mAuth;

    RecyclerView allQuestionsRecyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_feed);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionsFeed.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Intent intent = getIntent();
        name = intent.getStringArrayListExtra("UserNames");
        imageUrl = intent.getStringArrayListExtra("imageUrl");
        question = intent.getStringArrayListExtra("questions");
        imageUrll = intent.getStringExtra("imageUrll");
        userName = intent.getStringExtra("username");
        likedQuestions = intent.getStringArrayListExtra("likedQuestions");
        numberOfComments = intent.getStringArrayListExtra("numOfComments");
        numberOfLikes = intent.getStringArrayListExtra("numOfLikes");

        allQuestionsRecyclerView = findViewById(R.id.AllQuestionsRecyclerView);
        allQuestionsRecyclerView.setNestedScrollingEnabled(false);
        allQuestionsRecyclerView.setAdapter(new QuestionRecyclerAdapter(QuestionsFeed.this, imageUrl,name,question,likedQuestions,imageUrll,userName,numberOfLikes,numberOfComments));
        allQuestionsRecyclerView.setLayoutManager(new LinearLayoutManager(QuestionsFeed.this));

    }

}

