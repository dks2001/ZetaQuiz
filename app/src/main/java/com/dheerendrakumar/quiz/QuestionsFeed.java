package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

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
    String imageUrll="";

    boolean like;
    String userName="";
    FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_feed);

        mAuth = FirebaseAuth.getInstance();


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        questionsLinearLayout = findViewById(R.id.questionsLinearLayout);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        name = intent.getStringArrayListExtra("UserNames");
        imageUrl = intent.getStringArrayListExtra("imageUrl");
        question = intent.getStringArrayListExtra("questions");
        imageUrll = intent.getStringExtra("imageUrll");
        userName = intent.getStringExtra("username");


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                setQuestions();

            }
        },1000);

    }

    public void setQuestions() {

        for(int i=0;i<name.size();i++) {

            final  int k = i;

            LinearLayout questionTemplate = (LinearLayout) this.getLayoutInflater().inflate(R.layout.question_template, null);

            ImageView profile = questionTemplate.findViewById(R.id.UserNameProfilePic);
            TextView username = questionTemplate.findViewById(R.id.usernameSharedBy);
            TextView questionShared = questionTemplate.findViewById(R.id.questionShared);
            ImageView likePost = (ImageView) questionTemplate.findViewById(R.id.likePost);
            ImageView comments = (ImageView) questionTemplate.findViewById(R.id.comments);
            ImageView deletePost = (ImageView) questionTemplate.findViewById(R.id.deleteQuestion);

            if(imageUrl.get(i).equals("null")) {
                profile.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.profileicon, null));

            } else {
                profile.setImageBitmap(getBitmapFromURL(imageUrl.get(i)));
            }
            username.setText(name.get(i));
            questionShared.setText(question.get(i));

            questionsLinearLayout.addView(questionTemplate);
            like = false;

                likePost.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (likePost.getTag().toString().equals("unliked")) {
                            likePost.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.like_filled, null));

                            likePost.setTag("liked");
                            db.collection("user").document(mAuth.getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    DocumentSnapshot document = task.getResult();
                                    HashMap<String, Object> user = (HashMap<String, Object>) document.getData();


                                    ArrayList<String> likedPosts = (ArrayList<String>) document.get("likedPosts");

                                    if (likedPosts == null) {

                                        ArrayList<String> res = new ArrayList<String>();
                                        res.add(question.get(k));

                                        user.put("likedPosts", res);

                                        db.collection("user").document(mAuth.getUid())
                                                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(QuestionsFeed.this, "liked", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        likedPosts.add(question.get(k));

                                        user.put("likedPosts", likedPosts);

                                        db.collection("user").document(mAuth.getUid())
                                                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(QuestionsFeed.this, "liked", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            });

                        } else {

                            likePost.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.like_empty, null));
                            //like = false;
                            likePost.setTag("unliked");


                            db.collection("user").document(mAuth.getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    DocumentSnapshot document = task.getResult();
                                    HashMap<String, Object> user = (HashMap<String, Object>) document.getData();


                                    ArrayList<String> likedPosts = (ArrayList<String>) document.get("likedPosts");
                                    likedPosts.remove(question.get(k));

                                    user.put("likedPosts", likedPosts);

                                    db.collection("user").document(mAuth.getUid())
                                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(QuestionsFeed.this, "unliked", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });


                        }

                    }


                });

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(QuestionsFeed.this,AllUserComments.class);
                    intent.putExtra("question",question.get(k));
                    intent.putExtra("imageUrll",imageUrll);
                    intent.putExtra("username",userName);

                    startActivity(intent);

                }
            });

            deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(username.getText().toString().equals(userName)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsFeed.this);
                        builder.setMessage("Are you sure you want to delete the post?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                questionsLinearLayout.removeView(questionTemplate);

                                db.collection("Questions").document(question.get(k))
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(QuestionsFeed.this, "deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(QuestionsFeed.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.cancel();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                    } else {
                        Toast.makeText(QuestionsFeed.this, "You cannot delete someone's post.", Toast.LENGTH_SHORT).show();
                    }



                }
            });



        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", myBitmap + "");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

}

