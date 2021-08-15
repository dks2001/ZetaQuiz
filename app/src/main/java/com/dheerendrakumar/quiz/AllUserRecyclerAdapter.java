package com.dheerendrakumar.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AllUserRecyclerAdapter extends RecyclerView.Adapter<AllUserViewHolder>{

    ArrayList<String> name;
    ArrayList<String> username;
    ArrayList<String> imageurl;
    String myusername;
    ArrayList<String> friends;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference,FriendRequestRef;
    Context context;

    public AllUserRecyclerAdapter(Context context,ArrayList<String> name,ArrayList<String> username,ArrayList<String> imageurl,String myusername,ArrayList<String> friends) {
        this.context = context;
        this.myusername = myusername;
        this.name = name;
        this.friends = friends;
        this.username = username;
        this.imageurl = imageurl;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("friend_requests");
    }


    @NonNull
    @Override
    public AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_friend,parent,false);

        return new AllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserViewHolder holder, int position) {

        holder.getName().setText(name.get(position));
        holder.getUsername().setText(username.get(position));
        if(!imageurl.get(position).equals("null")) {
            Picasso.with(context).load(imageurl.get(position)).into(holder.getImage());
        } else {
            holder.getImage().setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.profileicon, null));
        }

        if(friends != null) {

            for(int s=0;s<friends.size();s++) {
                if(username.get(position).equals(friends.get(s))) {
                    //holder.getLikePost().setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.like_filled, null));
                    holder.getFollow().setText("unfollow");
                }
            }

        }

        MaintainFollowButton(holder,position);

        holder.getFollow().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.getFollow().getText().equals("follow")) {

                    holder.getFollow().setText("requested");
                    holder.getFollow().setBackgroundResource(R.drawable.requestedbg);

                    FriendRequestRef.child(myusername).child(username.get(position))
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                FriendRequestRef.child(username.get(position)).child(myusername)
                                        .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }

                        }
                    });





                } else if(holder.getFollow().getText().equals("unfollow")) {


                    holder.getFollow().setText("follow");

                    db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                HashMap<String, Object> res = (HashMap<String, Object>) documentSnapshot.getData();

                                if (username.get(position).equals(documentSnapshot.getString("username"))) {
                                    String id = documentSnapshot.getId();
                                    ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                                    friends.remove(myusername);
                                    res.put("friends", friends);

                                    db.collection("user").document(id)
                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else if (myusername.equals(documentSnapshot.getString("username"))) {

                                    ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                                    friends.remove(username.get(position));
                                    res.put("friends", friends);

                                    db.collection("user").document(mAuth.getUid())
                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }

                        }
                    });




                } else if(holder.getFollow().getText().equals("requested")) {

                    holder.getFollow().setText("follow");


                    final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("friend_requests").child(myusername).child(username.get(position));
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            dataSnapshot.getRef().removeValue();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("friend_requests").child(username.get(position)).child(myusername);
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            dataSnapshot.getRef().removeValue();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }



               /* if(holder.getFollow().getText().equals("follow")) {

                    holder.getFollow().setText("UNFOLLOW");

                    db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                HashMap<String, Object> res = (HashMap<String, Object>) documentSnapshot.getData();

                                if (username.get(position).equals(documentSnapshot.getString("username"))) {
                                    String id = documentSnapshot.getId();
                                    ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                                    ArrayList<String> chatList = (ArrayList<String>) documentSnapshot.get("chatList");
                                    friends.add(myusername);
                                    chatList.add(myusername);
                                    res.put("friends", friends);
                                    res.put("chatList",chatList);

                                    db.collection("user").document(id)
                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else if (myusername.equals(documentSnapshot.getString("username"))) {

                                    ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                                    ArrayList<String> chatList = (ArrayList<String>) documentSnapshot.get("chatList");
                                    friends.add(username.get(position));
                                    chatList.add(username.get(position));
                                    res.put("friends", friends);
                                    res.put("chatList",chatList);

                                    db.collection("user").document(mAuth.getUid())
                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }

                        }
                    });
                } else {

                    holder.getFollow().setText("follow");

                    db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                HashMap<String, Object> res = (HashMap<String, Object>) documentSnapshot.getData();

                                if (username.get(position).equals(documentSnapshot.getString("username"))) {
                                    String id = documentSnapshot.getId();
                                    ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                                    friends.remove(myusername);
                                    res.put("friends", friends);

                                    db.collection("user").document(id)
                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else if (myusername.equals(documentSnapshot.getString("username"))) {

                                    ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                                    friends.remove(username.get(position));
                                    res.put("friends", friends);

                                    db.collection("user").document(mAuth.getUid())
                                            .set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }

                        }
                    });

                } */

            }
        });

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public void MaintainFollowButton(AllUserViewHolder holder,int position) {

        FriendRequestRef.child(myusername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(username.get(position))) {

                            String request_type = snapshot.child(username.get(position)).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                holder.getFollow().setBackgroundResource(R.drawable.requestedbg);
                                holder.getFollow().setText("requested");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
