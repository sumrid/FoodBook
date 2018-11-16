package com.itkmitl59.foodbook.foodrecipe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itkmitl59.foodbook.MainActivity;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.profile.ProfileFragment;
import com.itkmitl59.foodbook.profile.User;
import com.itkmitl59.foodbook.profile.viewProfileActivity;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder>  {
    private ArrayList<FoodRecipe> mFoodRecipes;
    private Context mContext;
    private User userLink;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView foodImage,profileImage;
        public TextView foodName, foodDescription,foodOwner;
        public TextView likeCount, viewCount;
        public LinearLayout linkProfile;
        private ClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.food_image_item);
            foodName = itemView.findViewById(R.id.food_name_item);
            foodDescription = itemView.findViewById(R.id.food_description_item);
            profileImage = itemView.findViewById(R.id.profile_image);
            foodOwner = itemView.findViewById(R.id.food_owner_item);
            likeCount = itemView.findViewById(R.id.food_like_item);
            viewCount = itemView.findViewById(R.id.food_view_item);
            linkProfile = itemView.findViewById(R.id.link_profile);
            itemView.setOnClickListener(this);

        }

        public void setOnItemClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }


    public FoodListAdapter(ArrayList<FoodRecipe> dataSet, Context context) {
        this.mFoodRecipes = dataSet;
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final FoodRecipe foodRecipe = mFoodRecipes.get(position);
        setDisplayUser(holder,foodRecipe);
        Picasso.get().load(foodRecipe.getMainImageUrl()).fit().centerCrop().into(holder.foodImage);
        holder.foodName.setText(foodRecipe.getName());
        holder.foodDescription.setText(foodRecipe.getDescription());
        holder.viewCount.setText("views " + foodRecipe.getViews());
        holder.likeCount.setText("" + foodRecipe.getLike());

        holder.linkProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserData(foodRecipe.getOwner());
            }
        });


        holder.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startIntent(foodRecipe.getUid());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.foodlist_show_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return mFoodRecipes.size();
    }

    private void startIntent(String foodID){
        Intent intent = new Intent(mContext, FoodDetailActivity.class);
        intent.putExtra("id", foodID);
        mContext.startActivity(intent);
    }


    private void setDisplayUser(final FoodListAdapter.ViewHolder holder, final FoodRecipe item) {
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        store.collection("Users")
                .document(item.getOwner())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.foodOwner.setText(documentSnapshot.getString("displayName"));

                // get Image Profile from Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference fileUrl = storage.getReference("Users/"+item.getOwner());
                StorageReference fileRef = fileUrl.child("profile_img.jpg");
                fileRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri.toString()).fit().centerCrop().into(holder.profileImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

        });

    }

    private void viewUserData(final String userUid){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mFirestore.collection("Users").document(userUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    userLink = new User(documentSnapshot.getString("displayName"),
                            mAuth.getCurrentUser().getEmail(),
                            documentSnapshot.getString("phone"),
                            documentSnapshot.getString("aboutme"));

                    Intent intent = new Intent(mContext, viewProfileActivity.class);
                    intent.putExtra("curUser", userLink);
                    intent.putExtra("viewUid", userUid);
                    mContext.startActivity(intent);
                }
            }
        });
    }
}
