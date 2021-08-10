package com.dheerendrakumar.quiz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsViewHolder> {


    ArrayList<String> name;
    ArrayList<String> username;
    ArrayList<String> imageurl;
    String myUsername;
    Context context;

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

    }

    @Override
    public int getItemCount() {
        return name.size();
    }
}
