package com.itkmitl59.foodbook.comment;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipeAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<Comment> mComments;
    private Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView message;
        public TextView date;
        public ImageView userImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.comment_user_name);
            message = itemView.findViewById(R.id.comment_message);
            date = itemView.findViewById(R.id.comment_date);
            userImg = itemView.findViewById(R.id.comment_user_image);
        }
    }

    public CommentAdapter(List<Comment> mComments, Context mContext) {
        this.mComments = mComments;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment item = mComments.get(position);

        setDisplayUser(holder, item);
        holder.message.setText(item.getMessage());
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        holder.date.setText(format.format(item.getDate()));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    private void setDisplayUser(final CommentAdapter.MyViewHolder holder, final Comment item) {
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        store.collection("Users")
                .document(item.getUserID())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.userName.setText(documentSnapshot.getString("displayName"));

                // get Image Profile from Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference fileUrl = storage.getReference("Users/"+item.getUserID());
                StorageReference fileRef = fileUrl.child("profile_img.jpg");
                fileRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri.toString()).fit().centerCrop().into(holder.userImg);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

        });

    }
}
