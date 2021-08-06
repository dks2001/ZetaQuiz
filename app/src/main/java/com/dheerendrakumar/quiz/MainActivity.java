package com.dheerendrakumar.quiz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    String commentUrl="";
    PopupWindow popupWindow;
    int count=0;
    ImageView feed;
    HashMap<String,String> feedback = new HashMap<>();
    String imageIdentifier;
    String imageDownloadLink;
    ArrayList<String> scores;


    ArrayList<String> imageUrll;
    ArrayList<String> name;
    ArrayList<String> question;
    ArrayList<String> likedQuestions;
    Bitmap receivedImageBitmap;
    ImageView profileImageView;
    ArrayList<String> numberOfLikes;
    ArrayList<String> numberOfComments;


    ArrayList<String> myQuestions;
    ArrayList<String> likesOnMyPost;
    ArrayList<String> commentsOnMyPost;
    ArrayList<String> myLikesPosts;





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
                numberOfComments = new ArrayList<>();
                numberOfLikes = new ArrayList<>();

                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                DocumentReference docRef = db.collection("user").document(mAuth.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("", "DocumentSnapshot data: " + document.getData());
                                commentUrl = document.getString("imageUrl");

                            } else {
                                Log.d("", "No such document");
                            }
                        } else {
                            Log.d("", "get failed with ", task.getException());
                        }
                    }
                });

                db.collection("user").document(mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {

                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    likedQuestions = (ArrayList<String>)  documentSnapshot.get("likedPosts");
                                }
                            }
                        });

                db.collection("Questions")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                ArrayList<String> sharedBy = (ArrayList<String>) document.get("SharedBy");
                                                imageUrll.add(sharedBy.get(2));
                                                Log.i("urls",sharedBy.get(0));
                                                name.add(sharedBy.get(1));
                                                question.add(document.getId());
                                                numberOfComments.add(document.getString("numberOfComments"));
                                                numberOfLikes.add(document.getString("numberOfLikes"));



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
                        //intent.putStringArrayListExtra("imageUrl",imageUrll);
                        intent.putStringArrayListExtra("imageUrl",imageUrll);
                        intent.putStringArrayListExtra("questions",question);
                        intent.putStringArrayListExtra("likedQuestions",likedQuestions);
                        intent.putStringArrayListExtra("numOfLikes",numberOfLikes);
                        intent.putStringArrayListExtra("numOfComments",numberOfComments);
                        intent.putExtra("imageUrll",commentUrl);
                        intent.putExtra("username",username);
                        startActivity(intent);
                        progress.dismiss();
                    }
                },3000);


            }
        });




        navigationView = drawerLayout.findViewById(R.id.my_navigation_view);
        View view = navigationView.getHeaderView(0);
        profileImageView = (ImageView) view.findViewById(R.id.profilePic);
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView emailTextView = (TextView) view.findViewById(R.id.email);
        TextView changeProfile = (TextView) view.findViewById(R.id.changeProfile);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //changeProfile.setTextColor(Color.GREEN);

                if(android.os.Build.VERSION.SDK_INT>=23 && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);

                } else {
                    getChosenImage();
                }


            }
        });

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

                            Picasso.with(MainActivity.this).load(imageUrl).into(profileImageView);

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

                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                db.collection("user").document(mAuth.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        ArrayList<String> score = (ArrayList<String>) documentSnapshot.get("scores");
                        if(score==null) {
                            scores = new ArrayList<>();
                            for(int i=0;i<15;i++) {
                                scores.add("0.0");
                            }
                        } else {
                            scores = score;
                        }
                    }
                });

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, VocabularyActivity.class);
                        intent.putStringArrayListExtra("scores",scores);
                        startActivity(intent);
                        progress.dismiss();
                    }
                },1000);


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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;

        switch (id){

            case R.id.myAccount:

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
                        Intent intent = new Intent(MainActivity.this,MyAccount.class);
                        intent.putStringArrayListExtra("myQuestions",myQuestions);
                        intent.putStringArrayListExtra("myLikesPosts",myLikesPosts);
                        intent.putStringArrayListExtra("likesOnMyPost",likesOnMyPost);
                        intent.putStringArrayListExtra("commentsOnMyPost",commentsOnMyPost);
                        intent.putExtra("imageUrll",imageUrl);
                        intent.putExtra("email",email);
                        intent.putExtra("username",username);
                        startActivity(intent);
                    }
                },1000);


                break;

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

                            HashMap<String,Object> total = new HashMap<>();

                            //HashMap<String, ArrayList<String>> question = new HashMap<>();
                            ArrayList<String> userpost = new ArrayList<>();
                            userpost.add(mAuth.getUid());
                            userpost.add(username);
                            userpost.add(imageUrl);
                           // question.put("SharedBy",userpost);
                            total.put("SharedBy",userpost);
                            total.put("numberOfLikes","0");
                            total.put("numberOfComments","0");

                            db.collection("Questions").document(myQuestion.getText().toString())
                                    .set(total)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sharepopupWindow.dismiss();
                                            Toast.makeText(MainActivity.this, "Question Shared!", Toast.LENGTH_SHORT).show();

                                          /*  db.collection("Questions").document(myQuestion.getText().toString())
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    HashMap<String,Object> res = (HashMap<String, Object>) documentSnapshot.getData();

                                                    res.put("numberOfLikes",0);
                                                    res.put("numberOfComments",0);

                                                    db.collection("Questions").document(myQuestion.getText().toString())
                                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(MainActivity.this, "like comments done", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            }); */

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


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getChosenImage();
            }
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        try {

                            Intent selectedImage = result.getData();
                            Uri imageUri = selectedImage.getData();

                            Log.i("uriiii",imageUri+"");

                            String[] filePathcolumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getApplicationContext().getContentResolver().query(imageUri,filePathcolumn,null,null,null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathcolumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();

                            receivedImageBitmap = BitmapFactory.decodeFile(picturePath);
                            profileImageView.setImageBitmap(receivedImageBitmap);
                            profileImageView.setImageBitmap(receivedImageBitmap);
                            uploadImageToServer(receivedImageBitmap);


                            //Toast.makeText(MainActivity.this, new URL(picturePath.toString())+"", Toast.LENGTH_SHORT).show();
                        } catch(Exception e) {


                        }

                    }
                }
            });

    public void getChosenImage() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }


    private void uploadImageToServer(Bitmap receivedImageBitmap) {

        if(receivedImageBitmap !=null) {

            imageIdentifier = UUID.randomUUID() + ".png";

            // Get the data from an ImageView as bytes
            profileImageView.setDrawingCacheEnabled(true);
            profileImageView.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            receivedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(mAuth.getUid()).child(imageIdentifier).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            imageDownloadLink = task.getResult().toString();
                            Log.i("downloadlink",imageDownloadLink);
                            addImageDownLoadLink();
                        }

                    });
                }
            });
        }
    }

    public void addImageDownLoadLink() {

        DocumentReference docRef = db.collection("user").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        HashMap<String,Object> res = (HashMap<String, Object>) document.getData();

                        res.put("imageUrl",imageDownloadLink);

                        db.collection("user").document(mAuth.getUid())
                                .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

        db.collection("Questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("", document.getId() + " => " + document.getData());

                                         HashMap<String,Object> res = (HashMap<String, Object>) document.getData();

                                         for(String commenturl :res.keySet()) {
                                             if(commenturl.equals("SharedBy")) {
                                                 ArrayList<String> ques = (ArrayList) document.get("SharedBy");
                                                 if (ques.get(0).equals(mAuth.getUid())) {
                                                     ques.remove(2);
                                                     ques.add(2, imageDownloadLink);
                                                     res.put("SharedBy", ques);

                                                     db.collection("Questions").document(document.getId())
                                                             .set(res)
                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                     Toast.makeText(MainActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                                                                 }
                                                             });
                                                 }
                                             }else if(commenturl.equals(mAuth.getUid())) {

                                                 ArrayList<String> comm = (ArrayList<String>) document.get(mAuth.getUid());

                                                 comm.remove(1);
                                                 comm.add(1,imageDownloadLink);
                                                 res.put(mAuth.getUid(),comm);

                                                 db.collection("Questions").document(document.getId())
                                                         .set(res)
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                 Toast.makeText(MainActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                                                             }
                                                         });
                                             }
                                         }




                                    }

                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });







    }


}
