package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class AddFriend extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        recyclerView = findViewById(R.id.allUsers);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFriend.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ArrayList<String> name = getIntent().getStringArrayListExtra("name");
        ArrayList<String> username = getIntent().getStringArrayListExtra("username");
        ArrayList<String> imageurl = getIntent().getStringArrayListExtra("imageUrl");
        String myusername = getIntent().getStringExtra("myusername");
        ArrayList<String> friends = getIntent().getStringArrayListExtra("friends");

        Log.i("friends",friends+"");

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new AllUserRecyclerAdapter(AddFriend.this,name,username,imageurl,myusername,friends));
        recyclerView.setLayoutManager(new LinearLayoutManager(AddFriend.this));

    }
}