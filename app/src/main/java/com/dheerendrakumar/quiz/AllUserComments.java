package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllUserComments extends AppCompatActivity {

    FirebaseFirestore db;
    EditText comment;
    String myName;
    String ques;
    ImageView send;

    TextView commentOnQuestion;
    int count =0;

    String imageUrll="";
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    ArrayList<ArrayList<String>> AllComments = new ArrayList<>();
    LinearLayout commenSection;
    ArrayList<String> myComments;
    String userName="";
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user_comments);


        mAuth = FirebaseAuth.getInstance();
        myComments = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        comment = findViewById(R.id.myComment);
        send = findViewById(R.id.sendComment);
        commenSection = findViewById(R.id.commentSection);
        commentOnQuestion = findViewById(R.id.commentOnQuestion);

        Intent intent = getIntent();

        ques = intent.getStringExtra("question");
        imageUrll  = intent.getStringExtra("imageUrll");
        userName = intent.getStringExtra("username");


        commentOnQuestion.setText(ques);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setMyComment();

            }
        });

        db = FirebaseFirestore.getInstance();


        DocumentReference docRef = db.collection("Questions").document(ques);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> comments = document.getData();

                        for(String key : comments.keySet()) {

                            if(!key.equals("SharedBy") && !key.equals("numberOfLikes") && !key.equals("numberOfComments")) {

                                ArrayList<String> userComment = (ArrayList<String>) document.get(key);

                                names.add(userComment.get(0));
                                imageUrl.add(userComment.get(1));
                                ArrayList<String> myComment = new ArrayList<>();
                                for(int i=2;i<userComment.size();i++) {
                                    myComment.add(userComment.get(i));
                                }
                                AllComments.add(myComment);

                            } else if(key.equals("SharedBy")) {

                                ArrayList<String> userComment = (ArrayList<String>) document.get(key);
                                 myName = userComment.get(0);

                            }

                        }

                        //Log.d("", "DocumentSnapshot data: " + document.getData());
                    } else {
                        //Log.d("", "No such document");
                    }
                } else {
                    //Log.d("", "get failed with ", task.getException());
                }
            }
        });



        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setComments();
            }
        },1000);

    }

    public void setComments() {

        for(int i=0;i<names.size();i++) {


           ArrayList<String> oneUserComment = AllComments.get(i);

            for(int j=0;j<oneUserComment.size();j++) {

                LinearLayout questionTemplate = (LinearLayout) this.getLayoutInflater().inflate(R.layout.comments, null);

                ImageView profile = questionTemplate.findViewById(R.id.userCommentProfile);
                TextView username = questionTemplate.findViewById(R.id.UserCommentName);
                TextView comm = questionTemplate.findViewById(R.id.userComments);

                if(!imageUrl.get(i).equals("null")) {
                   // profile.setImageBitmap(getBitmapFromURL(imageUrl.get(i)));
                    Picasso.with(AllUserComments.this).load(imageUrl.get(i)).into(profile);
                } else {
                    profile.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.profileicon, null));
                }


                username.setText(names.get(i));

                comm.setText(oneUserComment.get(j));

                commenSection.addView(questionTemplate);

                comm.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if(username.getText().toString().equals(userName)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AllUserComments.this);
                            builder.setMessage("Are you sure you want to delete the comment");
                            builder.setCancelable(true);

                            builder.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            commenSection.removeView(questionTemplate);

                                            DocumentReference docRef = db.collection("Questions").document(ques);
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        ArrayList<String> commentsAfterDelete = (ArrayList<String>) document.get(userName);
                                                        int numOfComments = Integer.parseInt(document.getString("numberOfComments"));

                                                        commentsAfterDelete.remove(comm.getText().toString());

                                                        HashMap<String,Object> allComments = (HashMap<String, Object>) document.getData();
                                                        allComments.put(userName,commentsAfterDelete);
                                                        allComments.put("numberOfComments",numOfComments+"");

                                                        db.collection("Questions").document(ques)
                                                                .set(allComments).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Toast.makeText(AllUserComments.this, "deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(AllUserComments.this, "Failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            });




                                            dialog.cancel();
                                        }
                                    });

                            builder.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder.create();
                            alert11.show();
                        }

                        return true;
                    }
                });

            }

        }

    }

    public void setMyComment() {

        String myComment = comment.getText().toString();
        comment.setText("");

        LinearLayout questionTemplate = (LinearLayout) this.getLayoutInflater().inflate(R.layout.comments, null);

        ImageView profile = questionTemplate.findViewById(R.id.userCommentProfile);
        TextView username = questionTemplate.findViewById(R.id.UserCommentName);
        TextView comm = questionTemplate.findViewById(R.id.userComments);

        username.setText(userName);
        comm.setText(myComment);
        if(!imageUrll.equals("null")) {
           // profile.setImageBitmap(getBitmapFromURL(imageUrll));
            Picasso.with(AllUserComments.this).load(imageUrll).into(profile);
        } else {
            profile.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.profileicon, null));
        }

        commenSection.addView(questionTemplate);


        DocumentReference docRef = db.collection("Questions").document(ques);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                        HashMap<String, Object> addComment = (HashMap<String, Object>) document.getData();

                        int numOfComments = Integer.parseInt(document.getString("numberOfComments"))+1;
                        addComment.put("numberOfComments",numOfComments+"");


                        for (String key : addComment.keySet()) {

                            count++;

                            if (key.equals(userName)) {

                                ArrayList<String> com = (ArrayList<String>) document.get(key);
                                //addComment.remove(key);

                                com.add(myComment);
                                addComment.put(mAuth.getUid(), com);

                                db.collection("Questions").document(ques)
                                        .set(addComment)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d("", "DocumentSnapshot successfully written!");
                                                // Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                                                // startActivity(intent);



                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AllUserComments.this, "error120", Toast.LENGTH_SHORT).show();
                                                //Log.w("", "Error writing document", e);
                                            }
                                        });

                                count=0;
                                break;

                            }
                        }

                        if(count==addComment.size()) {
                            ArrayList<String> newComment = new ArrayList<>();
                            newComment.add(userName);
                            newComment.add(imageUrll);
                            newComment.add(myComment);

                            addComment.put(mAuth.getUid(),newComment);

                            db.collection("Questions").document(ques)
                                    .set(addComment)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                           // Log.d("", "DocumentSnapshot successfully written!");
                                            // Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                                            // startActivity(intent);


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                           // Log.w("", "Error writing document", e);
                                            Toast.makeText(AllUserComments.this, "error101", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }


                    }

                }
            }
        });



    }
}