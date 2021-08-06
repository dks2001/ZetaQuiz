package com.dheerendrakumar.quiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAccountRecyclerAdapter extends RecyclerView.Adapter<QuestionViewHolder> {

    ArrayList<String> question;
    ArrayList<String> likedQuestions;
    ArrayList<String> numberOfLikes;
    ArrayList<String> numberOfComments;
    String imageUrll="";
    String userName="";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;


    public MyAccountRecyclerAdapter(Context context,
                                   ArrayList<String> question,ArrayList<String> likedQuestions,
                                   String imageUrll,String userName,ArrayList<String> numberOfLikes,ArrayList<String> numberOfComments) {

        this.imageUrll = imageUrll;
        this.likedQuestions = likedQuestions;
        this.numberOfComments=numberOfComments;
        this.numberOfLikes = numberOfLikes;
        this.question = question;
        this.userName = userName;
        this.context=context;

    }


    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_template,parent,false);

        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {

        holder.getNoOfLikes().setText(numberOfLikes.get(position)+" likes");
        holder.getNoOfComments().setText(numberOfComments.get(position)+" comments");
        holder.getUsername().setText(userName);
        holder.getQuestionShared().setText(question.get(position));

        if (imageUrll.equals("null")) {
            holder.getProfilepic().setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.profileicon, null));

        } else {

            Picasso.with(context).load(imageUrll).into(holder.getProfilepic());
            //holder.getProfilepic().setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.profileicon, null));
        }

        if(likedQuestions != null) {

            for(int s=0;s<likedQuestions.size();s++) {
                if(question.get(position).equals(likedQuestions.get(s))) {
                    holder.getLikePost().setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.like_filled, null));
                    holder.getLikePost().setTag("liked");
                }
            }

        }

        holder.getLikePost().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                db.collection("Questions").document(question.get(position))
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot documentSnapshot = task.getResult();

                        HashMap<String,Object> ques = (HashMap<String, Object>) documentSnapshot.getData();

                        // String numOfLikes = documentSnapshot.getString("numberOfLikes");

                        int likes = Integer.parseInt(documentSnapshot.getString("numberOfLikes"))+1;

                        holder.getNoOfLikes().setText(likes+" likes");

                        ques.put("numberOfLikes",likes+"");

                        db.collection("Questions").document(question.get(position))
                                .set(ques).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "like done", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });




                if (holder.getLikePost().getTag().toString().equals("unliked")) {
                    holder.getLikePost().setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.like_filled, null));
                    holder.getLikePost().setTag("liked");



                    db.collection("user").document(mAuth.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot document = task.getResult();
                            HashMap<String, Object> user = (HashMap<String, Object>) document.getData();

                            ArrayList<String> likedPosts = (ArrayList<String>) document.get("likedPosts");

                            if (likedPosts == null) {

                                ArrayList<String> res = new ArrayList<String>();
                                res.add(question.get(position));

                                user.put("likedPosts", res);

                                db.collection("user").document(mAuth.getUid())
                                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                likedPosts.add(question.get(position));

                                user.put("likedPosts", likedPosts);

                                db.collection("user").document(mAuth.getUid())
                                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });

                } else {

                    holder.getLikePost().setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.like_empty, null));
                    //like = false;
                    holder.getLikePost().setTag("unliked");

                    db.collection("Questions").document(question.get(position))
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot documentSnapshot = task.getResult();

                            HashMap<String,Object> ques = (HashMap<String, Object>) documentSnapshot.getData();

                            // String numOfLikes = documentSnapshot.getString("numberOfLikes");

                            int dislike = Integer.parseInt(documentSnapshot.getString("numberOfLikes"))-1;

                            holder.getNoOfLikes().setText(dislike+" likes");
                            ques.put("numberOfLikes",dislike+"");

                            db.collection("Questions").document(question.get(position))
                                    .set(ques).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "like done", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });


                    db.collection("user").document(mAuth.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot document = task.getResult();
                            HashMap<String, Object> user = (HashMap<String, Object>) document.getData();


                            ArrayList<String> likedPosts = (ArrayList<String>) document.get("likedPosts");
                            likedPosts.remove(question.get(position));

                            user.put("likedPosts", likedPosts);

                            db.collection("user").document(mAuth.getUid())
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "unliked", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });



        holder.getCommentpost().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,AllUserComments.class);
                intent.putExtra("question",question.get(position));
                intent.putExtra("imageUrll",imageUrll);
                intent.putExtra("username",userName);

                context.startActivity(intent);

            }
        });




        holder.getDeletePost().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.getUsername().getText().toString().equals(userName)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete the post?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            db.collection("Questions").document(question.get(position))
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                                    int newPosition = holder.getAdapterPosition();
                                    question.remove(newPosition);
                                    notifyItemRemoved(newPosition);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "You cannot delete someone's post.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return question.size();
    }

}
