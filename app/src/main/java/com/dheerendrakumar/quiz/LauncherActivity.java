package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LauncherActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private static final int RC_SIGN_IN =1000 ;
    Button signinwithGoogle;
    TextView signUpTextView,usernameExist;
    EditText nameEditText,emailEditText,passwordEditText,usernameEdittext;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Button signinButton;
    GoogleSignInAccount account;
    FirebaseFirestore db;
    Handler handler;
    Map<String, Object> userrr;
    boolean exist = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        handler = new Handler();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        signinwithGoogle = findViewById(R.id.signinbuttongoogle);
        signUpTextView = findViewById(R.id.signuptextview);
        nameEditText = findViewById(R.id.nameEdittext);
        usernameExist = findViewById(R.id.usernameExist);
        usernameEdittext = findViewById(R.id.edtUsername);
        signinButton = findViewById(R.id.signinButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        TextView forgotPassword = findViewById(R.id.forgotPasswordtextView);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });


        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    if (signinButton.getText().equals("sign in")) {


                        if(emailEditText.getText().toString().equals("")) {
                            Toast.makeText(LauncherActivity.this, "email cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if(passwordEditText.getText().toString().equals("")) {
                            Toast.makeText(LauncherActivity.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {

                            ProgressDialog progress = new ProgressDialog(LauncherActivity.this);
                            progress.setTitle("Loading");
                            progress.setMessage("Wait while loading...");
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();


                            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                                    .addOnCompleteListener(LauncherActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("ok", "signInWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                progress.dismiss();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w("no", "signInWithEmail:failure", task.getException());
                                                Toast.makeText(LauncherActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                                progress.dismiss();
                                            }
                                        }
                                    });
                        }

                    } else {

                        if (nameEditText.getText().toString().equals("")) {
                            Toast.makeText(LauncherActivity.this, "name cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (emailEditText.getText().toString().equals("")) {
                            Toast.makeText(LauncherActivity.this, "email cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (passwordEditText.getText().toString().equals("")) {
                            Toast.makeText(LauncherActivity.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {

                            db.collection("user")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    String username = document.getString("username");
                                                    if(username.equals(usernameEdittext.getText().toString())) {
                                                        usernameExist.setText("Username already exist.");
                                                        usernameExist.setVisibility(View.VISIBLE);
                                                        exist = true;
                                                        break;
                                                    } else {
                                                        exist = false;
                                                    }


                                                    Log.d("", document.getId() + " => " + document.getData());
                                                }
                                                if(exist==false) {

                                                    createUser();

                                                }

                                            } else {
                                                Log.d("", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    }) ;


                        }
                    }
                }
        });



        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // nameTextView.setVisibility(View.VISIBLE);
                nameEditText.setVisibility(View.VISIBLE);
                usernameEdittext.setVisibility(View.VISIBLE);
                signinButton.setText("SIGN UP");
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

         signinwithGoogle.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 signIn();
             }
         });
    }

    public void createUser() {
        ProgressDialog progress = new ProgressDialog(LauncherActivity.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();


        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(LauncherActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ok", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("name", nameEditText.getText().toString());
                            newUser.put("email", emailEditText.getText().toString());
                            newUser.put("imageUrl", "null");
                            newUser.put("username", usernameEdittext.getText().toString());
                            ArrayList<String> friends = new ArrayList<>();
                            newUser.put("friends",friends);
                            addUser(task, newUser);

                            //Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                            //startActivity(intent);
                            progress.dismiss();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("no", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LauncherActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("pppp", "firebaseAuthWithGoogle:" + account.getEmail());

                userrr = new HashMap<>();
                userrr.put("name", account.getDisplayName());
                userrr.put("email", account.getEmail());
                userrr.put("imageUrl", account.getPhotoUrl()+"");
                SecureRandom random = new SecureRandom();
                int rand = random.nextInt(1000);
                userrr.put("username","guest"+rand);
                ArrayList<String> friends = new ArrayList<>();
                userrr.put("friends",friends);
                Log.i("image",account.getPhotoUrl()+"");

                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("oooooo", "Google sign in failed", e);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Toast.makeText(this, "already logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("pppp", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            addUser(task,userrr);



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("oooo", "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }



   /* public void addDataToFirebase(Task<AuthResult> taskk,Map<String, Object> newUser) {

        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String username = document.getString("username");
                                if(username.equals(usernameEdittext.getText().toString())) {
                                    usernameExist.setText("Username already exist.");
                                    usernameExist.setVisibility(View.VISIBLE);
                                    exist = true;
                                    break;
                                }


                                Log.d("", document.getId() + " => " + document.getData());
                            }
                            if(exist==false) {
                                addUser(taskk,newUser);
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                }) ;

    }  */

    public void resetPassword() {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View forgetPasswordView = layoutInflater.inflate(R.layout.forgot_password,null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(forgetPasswordView,width,height,focusable);

        EditText emailForPassword = (EditText) forgetPasswordView.findViewById(R.id.forgotpasswordEmail);
        Button cancel = (Button) forgetPasswordView.findViewById(R.id.cancelPasswordChange);
        Button confirm = (Button) forgetPasswordView.findViewById(R.id.confirmPasswordChange);

        popupWindow.showAtLocation(forgetPasswordView, Gravity.CENTER,0,0);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailForPassword.getText().toString().equals("")) {
                    Toast.makeText(LauncherActivity.this, "Enter your email id.", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    String emailAddress = emailForPassword.getText().toString();

                    firebaseAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LauncherActivity.this, "Password reset email send to the " + emailAddress, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LauncherActivity.this, "Something went wrong. Please Try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    public void addUser(Task<AuthResult> task,Map<String, Object> newUser) {
        db.collection("user").document(task.getResult().getUser().getUid())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("", "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("", "Error writing document", e);
                    }
                });

        mDatabase.child("users").child("uid").child(mAuth.getUid()).child("username").setValue(usernameEdittext.getText().toString());
    }

}