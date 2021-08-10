package com.dheerendrakumar.quiz;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllUserViewHolder extends RecyclerView.ViewHolder {


    TextView name,username;
    Button follow;
    ImageView image;


    public AllUserViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.friendName);
        username = itemView.findViewById(R.id.friendUsername);
        image = itemView.findViewById(R.id.friendprofile);
        follow = itemView.findViewById(R.id.follow);
    }

    public TextView getName() {
        return name;
    }

    public TextView getUsername() {
        return username;
    }

    public Button getFollow() {
        return follow;
    }

    public ImageView getImage() {
        return image;
    }
}
