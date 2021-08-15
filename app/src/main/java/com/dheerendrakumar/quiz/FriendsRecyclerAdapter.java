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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsViewHolder> {


    ArrayList<String> name;
    ArrayList<String> username;
    ArrayList<String> imageurl;
    String myUsername;
    Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FriendsRecyclerAdapter(Context context,ArrayList<String> name,ArrayList<String> username,ArrayList<String> imageurl,String myUsername) {
        this.context =context;
        this.name = name;
        this.username=username;
        this.imageurl=imageurl;
        this.myUsername = myUsername;
    }


    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friends,parent,false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {

        holder.getFriendname().setText(name.get(position));
        holder.getFriendusername().setText(username.get(position));
        if(!imageurl.get(position).equals("null")) {
            Picasso.with(context).load(imageurl.get(position)).into(holder.getImage());
        } else {
            holder.getImage().setImageResource(R.drawable.profileicon);
        }

        holder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("hisUsername",username.get(position));
                intent.putExtra("myUsername",myUsername);
                intent.putExtra("imageUrl",imageurl.get(position));
                context.startActivity(intent);
            }
        });

        holder.getLinearLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete user?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection("user").document(mAuth.getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                DocumentSnapshot documentSnapshot = task.getResult();
                                HashMap<String,Object> res = (HashMap<String, Object>) documentSnapshot.getData();
                                ArrayList<String> chat= (ArrayList<String>) documentSnapshot.get("chatList");

                                chat.remove(username.get(position));
                                res.put("chatList",chat);

                                db.collection("user").document(mAuth.getUid()).
                                        set(res).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                                        name.remove(holder.getAdapterPosition());
                                        username.remove(holder.getAdapterPosition());
                                        imageurl.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                    }
                                });

                            }
                        });

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return name.size();
    }
}
