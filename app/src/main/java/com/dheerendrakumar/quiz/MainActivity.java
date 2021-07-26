package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    Button randomQuiz;
    LinearLayout dailyQuiz;
    Button vocab;
    LinearLayout category;
    ImageView profileButton;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String myname="";
    String myemail="";

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
        //firebaseUser = mAuth.getCurrentUser();
        String uid = mAuth.getUid();
        Log.i("uidddddd",uid);




        NavigationView navigationView = findViewById(R.id.my_navigation_view);
        View view = navigationView.getHeaderView(0);
        ImageView profileImageView = (ImageView)view.findViewById(R.id.profilePic);
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView emailTextView = (TextView) view.findViewById(R.id.email);

        DocumentReference docRef = db.collection("user").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("", "DocumentSnapshot data: " + document.getData());
                        nameTextView.setText(document.getString("name"));
                        emailTextView.setText(document.getString("email"));

                        if(!document.getString("imageUrl").equals("null")) {
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



        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout navDrawer = findViewById(R.id.drawerLayout);
                // If the navigation drawer is not open then open it, if its already open then close it.
                if(!navDrawer.isDrawerOpen(GravityCompat.START)) {
                    navDrawer.openDrawer(GravityCompat.START);
                }
                else {
                    navDrawer.closeDrawer(GravityCompat.END);
                }
            }
        });



        for(int i=0;i<Category.category.length;i++) {
            final int j=i;

            LinearLayout categoryll = (LinearLayout) this.getLayoutInflater().inflate(R.layout.categoryview, null);
            Button categoryButton = categoryll.findViewById(R.id.categoryButtons);

            categoryButton.setText(Category.category[i]);
            category.addView(categoryll);

            categoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, Category.categorycode[j]+"", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,SpecificationsActivity.class);
                    intent.putExtra("categorycode",Category.categorycode[j]);
                    startActivity(intent);
                }
            });

        }


        vocab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,VocabularyActivity.class);
                startActivity(intent);
            }
        });


        randomQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,DailyQuizActivity.class);
                startActivity(intent);
            }
        });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap",myBitmap+"");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}