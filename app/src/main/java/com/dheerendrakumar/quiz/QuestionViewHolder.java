package com.dheerendrakumar.quiz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionViewHolder extends RecyclerView.ViewHolder {


    ImageView profilepic;
    ImageView deletePost;
    ImageView likePost;
    ImageView commentpost;
    TextView questionShared;
    TextView username;
    TextView noOfLikes;
    TextView noOfComments;


    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);

         profilepic = itemView.findViewById(R.id.UserNameProfilePic);
         username = itemView.findViewById(R.id.usernameSharedBy);
         questionShared = itemView.findViewById(R.id.questionShared);
         likePost = (ImageView) itemView.findViewById(R.id.likePost);
         commentpost = (ImageView) itemView.findViewById(R.id.comments);
         deletePost = (ImageView) itemView.findViewById(R.id.deleteQuestion);
         noOfComments = itemView.findViewById(R.id.numberOfComments);
         noOfLikes = itemView.findViewById(R.id.numberOfLikes);

    }

    public ImageView getProfilepic() {
        return profilepic;
    }

    public ImageView getDeletePost() {
        return deletePost;
    }

    public ImageView getLikePost() {
        return likePost;
    }

    public ImageView getCommentpost() {
        return commentpost;
    }

    public TextView getQuestionShared() {
        return questionShared;
    }

    public TextView getUsername() {
        return username;
    }

    public TextView getNoOfLikes() {
        return noOfLikes;
    }

    public TextView getNoOfComments() {
        return noOfComments;
    }
}
