package com.dheerendrakumar.quiz;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    TextView friendname,friendusername;
    ImageView image;
    LinearLayout linearLayout;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        friendname = itemView.findViewById(R.id.friendname);
        friendusername = itemView.findViewById(R.id.friendusername);
        image = itemView.findViewById(R.id.friendprofilepic);
        linearLayout = itemView.findViewById(R.id.openchatwithfriend);
    }

    public TextView getFriendname() {
        return friendname;
    }

    public TextView getFriendusername() {
        return friendusername;
    }

    public ImageView getImage() {
        return image;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }
}
