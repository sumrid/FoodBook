package com.itkmitl59.foodbook.foodrecipe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itkmitl59.foodbook.MainActivity;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.profile.ProfileFragment;
import com.itkmitl59.foodbook.profile.User;
import com.itkmitl59.foodbook.profile.editProfileActivity;
import com.itkmitl59.foodbook.profile.viewProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PoppularRecipeAdapter extends RecyclerView.Adapter<PoppularRecipeAdapter.MyViewHolder> {

    private Context mContext;
    private List<FoodRecipe> recipes = new ArrayList<>();
    private User userLink;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView image,profileImage;
        public TextView name,like,owner;
        public LinearLayout linkProfile;
        private View.OnClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.popular_image_item);
            name = itemView.findViewById(R.id.popular_name_item);
            like = itemView.findViewById(R.id.popular_like);
            owner = itemView.findViewById(R.id.popular_owner_item);
            profileImage = itemView.findViewById(R.id.profile_image);
            linkProfile = itemView.findViewById(R.id.link_profile);
        }
    }

    // Constructor
    public PoppularRecipeAdapter(Context mContext) {
        this.mContext = mContext;
        loadFoodRecipe();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popular_food_item, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final FoodRecipe item = recipes.get(i);

        setDisplayUser(myViewHolder, item);
        Picasso.get().load(item.getMainImageUrl()).fit().centerCrop().into(myViewHolder.image);
        myViewHolder.name.setText(item.getName());
        myViewHolder.like.setText(item.getLike() + " likes");
        myViewHolder.owner.setText("User");

        myViewHolder.linkProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserData(item.getOwner());
            }
        });

        myViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private void loadFoodRecipe() {
        firestore.collection("FoodRecipes")
                .orderBy("like", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        recipes.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            FoodRecipe item = document.toObject(FoodRecipe.class);
                            item.setUid(document.getId());
                            recipes.add(item);
                            Log.d("Adapter", "load food");
                        }
                        notifyDataSetChanged();
                    }
                });
    }

    public void startIntent(int i) {
        FoodRecipe recipe = recipes.get(i);

        Intent intent = new Intent(mContext, FoodDetailActivity.class);
        intent.putExtra("id", recipe.getUid());
        mContext.startActivity(intent);
    }


    private void setDisplayUser(final PoppularRecipeAdapter.MyViewHolder holder, final FoodRecipe item) {
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        store.collection("Users")
                .document(item.getOwner())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.owner.setText(documentSnapshot.getString("displayName"));

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
