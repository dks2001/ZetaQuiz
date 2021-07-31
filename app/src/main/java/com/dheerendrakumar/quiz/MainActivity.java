package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    Button randomQuiz;
    LinearLayout dailyQuiz;
    Button vocab;
    LinearLayout category;
    ImageView profileButton;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    String username="";
    String email="";
    String imageUrl="";
    PopupWindow popupWindow;
    int count=0;
    ImageView feed;
    HashMap<String,String> feedback = new HashMap<>();


    ArrayList<String> imageUrll;
    ArrayList<String> name;
    ArrayList<String> question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        randomQuiz = findViewById(R.id.randomQuiz);
        dailyQuiz = findViewById(R.id.dailyquiz);
        vocab = findViewById(R.id.vocabImageview);
        category = findViewById(R.id.categoryll);
        profileButton = findViewById(R.id.profileButton);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawerLayout);
        feed = findViewById(R.id.feed);

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageUrll = new ArrayList<>();
                name = new ArrayList<>();
                question = new ArrayList<>();

                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                db.collection("Questions")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Map<String, Object> questionSharedBy = document.getData();

                                        for(String key:questionSharedBy.keySet()) {

                                            if (key.equals("SharedBy")) {


                                                ArrayList<String> sharedBy = (ArrayList<String>) document.get("SharedBy");
                                                imageUrll.add(sharedBy.get(1));
                                                name.add(sharedBy.get(0));
                                                question.add(document.getId());

                                            }


                                        }

                                        Log.d("", document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Log.d("", "Error getting documents: ", task.getException());
                                }
                            }
                        }) ;

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this,QuestionsFeed.class);
                        intent.putStringArrayListExtra("UserNames",name);
                        intent.putStringArrayListExtra("imageUrl",imageUrll);
                        intent.putStringArrayListExtra("questions",question);
                        intent.putExtra("imageUrll",imageUrl);
                        intent.putExtra("username",username);
                        startActivity(intent);
                        progress.dismiss();
                    }
                },2000);


            }
        });


        navigationView = drawerLayout.findViewById(R.id.my_navigation_view);
        View view = navigationView.getHeaderView(0);
        ImageView profileImageView = (ImageView) view.findViewById(R.id.profilePic);
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView emailTextView = (TextView) view.findViewById(R.id.email);

        DocumentReference docRef = db.collection("user").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("", "DocumentSnapshot data: " + document.getData());
                        username = document.getString("name");
                        email = document.getString("email");
                        imageUrl = document.getString("imageUrl");
                        nameTextView.setText(document.getString("name"));
                        emailTextView.setText(document.getString("email"));

                        if (!document.getString("imageUrl").equals("null")) {
                            profileImageView.setImageBitmap(getBitmapFromURL(document.getString("imageUrl")));

                        } else {
                            profileImageView.setImageResource(R.drawable.profileicon);
                        }

                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout navDrawer = findViewById(R.id.drawerLayout);
                // If the navigation drawer is not open then open it, if its already open then close it.
                if (!navDrawer.isDrawerOpen(GravityCompat.START)) {
                    navDrawer.openDrawer(GravityCompat.START);
                } else {
                    navDrawer.closeDrawer(GravityCompat.END);
                }
            }
        });


        for (int i = 0; i < Category.category.length; i++) {
            final int j = i;

            LinearLayout categoryll = (LinearLayout) this.getLayoutInflater().inflate(R.layout.categoryview, null);
            Button categoryButton = categoryll.findViewById(R.id.categoryButtons);

            categoryButton.setText(Category.category[i]);
            category.addView(categoryll);

            categoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, Category.categorycode[j] + "", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SpecificationsActivity.class);
                    intent.putExtra("categorycode", Category.categorycode[j]);
                    startActivity(intent);
                }
            });

        }


        vocab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, VocabularyActivity.class);
                startActivity(intent);
            }
        });


        randomQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DailyQuizActivity.class);
                startActivity(intent);
            }
        });
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



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        switch (id){

            case R.id.shareQuestion:

                LayoutInflater sharelayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View shareview = sharelayoutInflater.inflate(R.layout.share_question,null);


                PopupWindow sharepopupWindow = new PopupWindow(shareview,width,height,focusable);

                Button cancelShare = (Button) shareview.findViewById(R.id.cancelShareQuestion);
                Button share = (Button) shareview.findViewById(R.id.confirmQuestionShare);
                EditText myQuestion = (EditText) shareview.findViewById(R.id.sharemyQuestion);

                sharepopupWindow.showAtLocation(shareview,Gravity.CENTER,0,0);
                cancelShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharepopupWindow.dismiss();
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(myQuestion.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "Please write something...", Toast.LENGTH_SHORT).show();
                        } else {

                            HashMap<String, ArrayList<String>> question = new HashMap<>();
                            ArrayList<String> userpost = new ArrayList<>();
                            userpost.add(username);
                            userpost.add(imageUrl);
                            question.put("SharedBy",userpost);

                            db.collection("Questions").document(myQuestion.getText().toString())
                                    .set(question)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sharepopupWindow.dismiss();
                                            Toast.makeText(MainActivity.this, "Question Shared!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            sharepopupWindow.dismiss();
                                            Toast.makeText(MainActivity.this, "Failed ! Try again later..", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }


                    }
                });


                break;

            case R.id.changePassword:


                LayoutInflater changePasswordinflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View changePasswordView = changePasswordinflator.inflate(R.layout.change_password,null);

                PopupWindow changePasswordPopup = new PopupWindow(changePasswordView,width,height,focusable);

                EditText oldPassword = (EditText) changePasswordView.findViewById(R.id.oldPassword);
                EditText newPassword = (EditText) changePasswordView.findViewById(R.id.newPassword);
                EditText confirmNewPassword = (EditText) changePasswordView.findViewById(R.id.confirmNewPassword);
                Button cancelPasswordChange = (Button) changePasswordView.findViewById(R.id.cancelPasswordChange);
                Button confirmPasswordChange = (Button) changePasswordView.findViewById(R.id.confirmPasswordChange);


                changePasswordPopup.showAtLocation(changePasswordView,Gravity.CENTER,0,0);

                cancelPasswordChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePasswordPopup.dismiss();
                    }
                });

                confirmPasswordChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(oldPassword.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "Old Password cannot be empty.", Toast.LENGTH_SHORT).show();
                        } else if(newPassword.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "New password cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if(confirmNewPassword.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "confirm new password", Toast.LENGTH_SHORT).show();
                        } else if(!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Re enter new password", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword.getText().toString());

                            firebaseUser.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {
                                                firebaseUser.updatePassword(confirmNewPassword.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(MainActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                                            changePasswordPopup.dismiss();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Error...Try Again Later", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });


                break;

            case R.id.feedback:

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.feedback_layout,null);


                popupWindow = new PopupWindow(view,width,height,focusable);

                Button cancel = (Button) view.findViewById(R.id.cancelFeedback);
                Button submitFeedback = (Button) view.findViewById(R.id.submitFeedback);
                EditText myfeedback = (EditText) view.findViewById(R.id.myFeedback);

                popupWindow.showAtLocation(view,Gravity.CENTER,0,0);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                submitFeedback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        count++;
                        feedback.put(String.valueOf(count),myfeedback.getText().toString());

                        db.collection("feedbacks").document(username)
                                .set(feedback, SetOptions.merge());
                        popupWindow.dismiss();
                        Toast.makeText(MainActivity.this, "feedback submitted", Toast.LENGTH_SHORT).show();

                    }
                });


                break;



            case R.id.shareApp :
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;

            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this,LauncherActivity.class);
                startActivity(intent);
                finish();


            default:


                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        popupWindow.dismiss();
    }
}