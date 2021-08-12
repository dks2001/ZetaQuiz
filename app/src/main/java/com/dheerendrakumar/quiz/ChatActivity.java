package com.dheerendrakumar.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ImageButton send;
    boolean notify = false;
    EditText msg;
    String myUsername;
    String hisUsername;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
    RecyclerView recyclerView;
    DatabaseReference users;
    FirebaseDatabase firebaseDatabase;
    TextView name, userstatus;
    String imageUrl;
    ImageView profiletv;
    String myuid;
    RelativeLayout relativeLayout;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseDatabase = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();

         hisUsername = getIntent().getStringExtra("hisUsername");
         myUsername = getIntent().getStringExtra("myUsername");
         imageUrl = getIntent().getStringExtra("imageUrl");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.chatrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        users = firebaseDatabase.getReference("users");
        profiletv = findViewById(R.id.profiletv);

        send = findViewById(R.id.sendmsg);
        msg = findViewById(R.id.messaget);
        name = findViewById(R.id.nameptv);
        userstatus = findViewById(R.id.onlinetv);
        relativeLayout = findViewById(R.id.rootview);

        checkUserStatus();


        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                relativeLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = relativeLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {

                    checkTypingStatus(hisUsername);
                    //Toast.makeText(ChatActivity.this,"Keyboard is showing",Toast.LENGTH_LONG).show();
                } else {
                    checkTypingStatus("noOne");
                    //Toast.makeText(ChatActivity.this,"keyboard closed",Toast.LENGTH_LONG).show();
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("user").document(myuid).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                DocumentSnapshot documentSnapshot = task.getResult();
                                HashMap<String,Object> res = (HashMap<String, Object>) documentSnapshot.getData();
                               // ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                                //friends.remove(hisUsername);
                                //friends.add(0,hisUsername);
                                ArrayList<String> chatList = (ArrayList<String>) documentSnapshot.get("chatList");
                                if(!chatList.contains(hisUsername)) {
                                    chatList.add(0,hisUsername);
                                } else {
                                    chatList.remove(hisUsername);
                                    chatList.add(0,hisUsername);
                                }
                                //res.put("friends",friends);
                                res.put("chatList",chatList);



                                db.collection("user").document(myuid)
                                        .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ChatActivity.this, "first", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });

                checkTypingStatus("noOne");
                notify = true;
                String message = msg.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {//if empty
                    Toast.makeText(ChatActivity.this, "Please Write Something Here", Toast.LENGTH_LONG).show();
                } else {
                    sendmessage(message);
                }
                msg.setText("");
            }
        });
        name.setText(hisUsername);
        if(imageUrl.equals("null")) {
            profiletv.setImageResource(R.drawable.profileicon);
        } else {
            Picasso.with(ChatActivity.this).load(imageUrl).into(profiletv);
        }


        Query userquery = users.orderByChild("username").equalTo(hisUsername);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // retrieve user data
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //String nameh = "" + dataSnapshot1.child("name").getValue();
                    String onlinestatus = "" + dataSnapshot1.child("onlineStatus").getValue();
                    String typingto = "" + dataSnapshot1.child("typingTo").getValue();
                    if (typingto.equals(myUsername)) {// if user is typing to my chat
                        userstatus.setText("Typing....");// type status as typing
                    } else {
                        if (onlinestatus.equals("online")) {
                            userstatus.setText(onlinestatus);
                        } else {
                            if(onlinestatus.equals("null")) {
                                userstatus.setText("Last seen: Today");
                            } else {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(Long.parseLong(onlinestatus));
                                String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                                userstatus.setText("Last Seen:" + timedate);
                            }
                        }
                    }
                    //name.setText(nameh);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        readMessages();
    }


    private void sendmessage(final String message) {
        // creating a reference to store data in firebase
        // We will be storing data using current time in "Chatlist"
        // and we are pushing data using unique id in "Chats"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUsername);
        hashMap.put("receiver", hisUsername);
        hashMap.put("messsage", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("dilihat", false);
        hashMap.put("type", "text");
        databaseReference.child("Chats").push().setValue(hashMap);
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ChatList").child(hisUsername).child(myUsername);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref1.child("id").setValue(myUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(myUsername).child(hisUsername);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    ref2.child("id").setValue(hisUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {
        // show message after retrieving data
        chatList = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);
                    if (modelChat.getSender().equals(myUsername) &&
                            modelChat.getReceiver().equals(hisUsername) ||
                            modelChat.getReceiver().equals(myUsername)
                                    && modelChat.getSender().equals(hisUsername)) {
                        chatList.add(modelChat); // add the chat in chatlist
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList,myUsername);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
       // checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
    }

    @Override
    protected void onResume() {
       // checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void checkOnlineStatus(String status) {
        // check online status
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users").child(myuid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        dbref.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users").child(myuid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        dbref.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myuid = user.getUid();
        }

    }

}