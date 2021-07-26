package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class LauncherActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =1000 ;
    Button signinwithGoogle;
    TextView signUpTextView,nameTextView;
    EditText nameEditText,emailEditText,passwordEditText;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Button signinButton;
    GoogleSignInAccount account;
    FirebaseFirestore db;
    Handler handler;
    Map<String, Object> userrr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        handler = new Handler();

        signinwithGoogle = findViewById(R.id.signinbuttongoogle);
        signUpTextView = findViewById(R.id.signuptextview);
        nameEditText = findViewById(R.id.nameEdittext);
        nameTextView = findViewById(R.id.nameTextview);
        signinButton = findViewById(R.id.signinButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);




        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progress = new ProgressDialog(LauncherActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                    if (signinButton.getText().equals("sign in")) {


                        if(emailEditText.getText().toString().equals("")) {
                            Toast.makeText(LauncherActivity.this, "email cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if(passwordEditText.getText().toString().equals("")) {
                            Toast.makeText(LauncherActivity.this, "password cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {


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
                                               addDataToFirebase(task,newUser);

                                                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                                                startActivity(intent);
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
                    }
                }
        });



        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextView.setVisibility(View.VISIBLE);
                nameEditText.setVisibility(View.VISIBLE);
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

                            addDataToFirebase(task,userrr);



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("oooo", "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    public void addDataToFirebase(Task<AuthResult> task,Map<String, Object> newUser) {

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
    }

}