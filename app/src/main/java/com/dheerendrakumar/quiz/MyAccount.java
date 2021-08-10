package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAccount extends AppCompatActivity {

    ArrayList<String> question = new ArrayList<>();
    ArrayList<String> likedQuestions = new ArrayList<>();
    ArrayList<String> numberOfLikes = new ArrayList<>();
    ArrayList<String> numberOfComments = new ArrayList<>();
    String imageUrll="";
    String userName="";
    String email="";

    RecyclerView myPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        ImageView myProfile = findViewById(R.id.friendprofile);
        TextView username = findViewById(R.id.myAccountUsername);
        TextView emaill = findViewById(R.id.myAccountEmail);

        Intent intent = getIntent();
        question = intent.getStringArrayListExtra("myQuestions");
        imageUrll = intent.getStringExtra("imageUrll");
        userName = intent.getStringExtra("username");
        likedQuestions = intent.getStringArrayListExtra("myLikesPosts");
        numberOfComments = intent.getStringArrayListExtra("commentsOnMyPost");
        numberOfLikes = intent.getStringArrayListExtra("likesOnMyPost");
        email = intent.getStringExtra("email");

        if(!imageUrll.equals("null")) {
            Picasso.with(MyAccount.this).load(imageUrll).into(myProfile);
        } else {
            myProfile.setImageResource(R.drawable.profileicon);
        }
        username.setText(userName);
        emaill.setText(email);

        Log.i("ques",question+"");
        Log.i("comm",numberOfComments+"");
        Log.i("likes",numberOfLikes+"");
        Log.i("likedques",likedQuestions+"");
        Log.i("email",email+"");
        Log.i("imageurl",imageUrll+"");


        myPosts = findViewById(R.id.myPosts);

        myPosts.setNestedScrollingEnabled(false);
        myPosts.setAdapter(new MyAccountRecyclerAdapter(MyAccount.this,question,likedQuestions,imageUrll,userName,numberOfLikes,numberOfComments));
        myPosts.setLayoutManager(new LinearLayoutManager(MyAccount.this));
    }
}