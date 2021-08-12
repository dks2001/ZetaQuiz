package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAccount extends AppCompatActivity {

    String imageUrll="";
    String userName="";
    String email="";
    String name="";
    TextView numOfPosts;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    boolean exist = false;

    ArrayList<String> myQuestions = new ArrayList<>();
    ArrayList<String> likesOnMyPost = new ArrayList<>() ;
    ArrayList<String> commentsOnMyPost = new ArrayList<>();
    ArrayList<String> myLikesPosts = new ArrayList<>();

    ArrayList<String> friendsname = new ArrayList<>();
    ArrayList<String> friendsImageUrl = new ArrayList<>();
    ArrayList<String> myfriends = new ArrayList<>();
    ArrayList<String> friendsusername = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ImageView myProfile = findViewById(R.id.friendprofile);
        EditText username = findViewById(R.id.myusername);
        ImageView editusername = findViewById(R.id.editUsername);
        TextView emaill = findViewById(R.id.myEmail);
        TextView myname = findViewById(R.id.myname);
        numOfPosts = findViewById(R.id.numberOfPosts);
        TextView numOfFriends = findViewById(R.id.numberOfFriends);

        numOfFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendsname = new ArrayList<>();
                friendsImageUrl = new ArrayList<>();
                myfriends = new ArrayList<>();
                friendsusername = new ArrayList<>();

                db.collection("user").document(mAuth.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot documentSnapshot = task.getResult();
                        myfriends = (ArrayList<String>) documentSnapshot.get("friends");

                    }
                });

                db.collection("user").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                for(int i=0;i<myfriends.size();i++) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        String username = document.getString("username");
                                        if (username.equals(myfriends.get(i))) {
                                            friendsname.add(document.getString("name"));
                                            friendsImageUrl.add(document.getString("imageUrl"));
                                            friendsusername.add(document.getString("username"));
                                        }
                                    }

                                }


                            }
                        });

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MyAccount.this,AddFriend.class);
                        intent.putStringArrayListExtra("name",friendsname);
                        intent.putStringArrayListExtra("username",friendsusername);
                        intent.putStringArrayListExtra("imageUrl",friendsImageUrl);
                        intent.putExtra("myusername",userName);
                        intent.putStringArrayListExtra("friends",myfriends);
                        startActivity(intent);
                    }
                },1000);


            }
        });


        editusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editusername.getTag().equals("editmyusername")) {
                    username.setEnabled(true);
                    editusername.setTag("update");
                    editusername.setImageResource(R.drawable.updateusername);
                } else {

                    db.collection("user")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            String usernamee = document.getString("username");
                                            if(usernamee.equals(username.getText().toString())) {
                                                username.setText("Username already exist.");
                                                username.setVisibility(View.VISIBLE);
                                                exist = true;
                                                break;
                                            } else {
                                                exist = false;
                                            }


                                            Log.d("", document.getId() + " => " + document.getData());
                                        }
                                        if(exist==false) {


                                            db.collection("user").document(mAuth.getUid())
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    HashMap<String,Object> res = (HashMap<String, Object>) documentSnapshot.getData();
                                                    //String usernamee = documentSnapshot.getString("username");
                                                    res.put("username",username.getText().toString());

                                                    db.collection("user").document(mAuth.getUid())
                                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(MyAccount.this, "updated", Toast.LENGTH_SHORT).show();
                                                            editusername.setImageResource(R.drawable.edit);
                                                        }
                                                    });

                                                }
                                            });

                                            username.setEnabled(false);
                                            editusername.setTag("editmyusername");

                                        }

                                    } else {
                                        Log.d("", "Error getting documents: ", task.getException());
                                    }
                                }
                            }) ;

                }
            }
        });


        Intent intent = getIntent();
        imageUrll = intent.getStringExtra("imageUrll");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        userName = intent.getStringExtra("username");


        if(!imageUrll.equals("null")) {
            Picasso.with(MyAccount.this).load(imageUrll).into(myProfile);
        } else {
            myProfile.setImageResource(R.drawable.profileicon);
        }
        myname.setText(name);
        emaill.setText(email);
        username.setText(userName);

        numOfPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myQuestions = new ArrayList<>();
                likesOnMyPost = new ArrayList<>() ;
                commentsOnMyPost = new ArrayList<>();
                myLikesPosts = new ArrayList<>();


                db.collection("Questions")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        ArrayList<String> sharedBy = (ArrayList<String>) document.get("SharedBy");
                                        if(sharedBy.get(0).equals(mAuth.getUid())) {
                                            myQuestions.add(document.getId());
                                            likesOnMyPost.add(document.getString("numberOfLikes"));
                                            commentsOnMyPost.add(document.getString("numberOfComments"));
                                        }

                                        Log.d("", document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Log.d("", "Error getting documents: ", task.getException());
                                }
                            }
                        }) ;

                db.collection("user").document(mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {

                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    myLikesPosts = (ArrayList<String>)  documentSnapshot.get("likedPosts");


                                }
                            }
                        });

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MyAccount.this,MyPosts.class);
                        intent.putStringArrayListExtra("myQuestions",myQuestions);
                        intent.putStringArrayListExtra("myLikesPosts",myLikesPosts);
                        intent.putStringArrayListExtra("likesOnMyPost",likesOnMyPost);
                        intent.putStringArrayListExtra("commentsOnMyPost",commentsOnMyPost);
                        intent.putExtra("imageUrll",imageUrll);
                        intent.putExtra("email",email);
                        intent.putExtra("username",userName);
                        startActivity(intent);
                    }
                },1000);
            }
        });


    }
}