package com.dheerendrakumar.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class levelRecyclerAdapter extends  RecyclerView.Adapter<levelViewHolder> {

    private Context context;
    String[] levels = {"level 1","level 2","level 3","level 4","level 5","level 6","level 7","level 8","level 9","level 10","level 11","level 12","level 13","level 14","level 15"};

    Handler handler = new Handler();
    ArrayList<String> questions;
    ArrayList<String> corrects;
    ArrayList<ArrayList<String>> incorrects;
    ArrayList<String> scores;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public levelRecyclerAdapter(Context context,ArrayList<String> scores) {
        this.context = context;
        this.scores = scores;
    }


    @NonNull
    @Override
    public levelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.levelview_holder,parent,false);

        return new levelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull levelViewHolder holder, int position) {

        holder.getLevelButton().setText(levels[position]);
        holder.getScoretextView().setText(scores.get(position));


        if(scores.get(position).equals("0.0")) {

            if(position==0) {
                holder.getLevelButton().setEnabled(true);
            } else if(!scores.get(position-1).equals("0.0")) {
                holder.getLevelButton().setEnabled(true);
            } else {
                holder.getLevelButton().setEnabled(false);
            }

        } else {
            holder.getLevelButton().setEnabled(true);
        }



        holder.getLevelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progress = new ProgressDialog(context);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                questions = new ArrayList<>();
                corrects = new ArrayList<>();
                incorrects = new ArrayList<>();


                db.collection(holder.getLevelButton().getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("", document.getId() + " => " + document.getData());
                                        questions.add(document.getId());
                                        corrects.add(document.getString("correct"));
                                        ArrayList<String> group = (ArrayList<String>) document.get("incorrect");
                                        incorrects.add(group);
                                        Log.i("incor",group.get(0));

                                    }

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            Intent intent = new Intent(context,VocabQuizActivity.class);
                                            intent.putStringArrayListExtra("questionTexts",questions);
                                            intent.putStringArrayListExtra("correctAnswer",corrects);
                                            Bundle args = new Bundle();
                                            args.putSerializable("ARRAYLIST",(Serializable)incorrects);
                                            intent.putExtra("BUNDLE",args);
                                            intent.putExtra("level",position);
                                            context.startActivity(intent);
                                            progress.dismiss();
                                        }
                                    },2000);

                                } else {
                                    Log.d("", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return levels.length;
    }
}
