package com.dheerendrakumar.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class Friends extends AppCompatActivity {

    RecyclerView myfriends;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        myfriends = findViewById(R.id.myfriends);
        mAuth = FirebaseAuth.getInstance();

        ArrayList<String> name = getIntent().getStringArrayListExtra("name");
        ArrayList<String> username = getIntent().getStringArrayListExtra("username");
        ArrayList<String> imageurl = getIntent().getStringArrayListExtra("imageurl");
        String myUsername = getIntent().getStringExtra("myUsername");

        myfriends.setNestedScrollingEnabled(false);
        myfriends.setAdapter(new FriendsRecyclerAdapter(Friends.this,name,username,imageurl,myUsername));
        myfriends.setLayoutManager(new LinearLayoutManager(Friends.this));

    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        // checkOnlineStatus(timestamp);
       // checkTypingStatus("noOne");
    }

    @Override
    protected void onResume() {
         checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void checkOnlineStatus(String status) {
        // check online status
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        dbref.updateChildren(hashMap);
    }

   /* private void checkTypingStatus(String typing) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users").child(myuid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        dbref.updateChildren(hashMap);
    } */

    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }

}