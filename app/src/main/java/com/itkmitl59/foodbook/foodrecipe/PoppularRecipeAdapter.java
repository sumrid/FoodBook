package com.itkmitl59.foodbook.foodrecipe;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PoppularRecipeAdapter extends RecyclerView.Adapter<PoppularRecipeAdapter.MyViewHolder> {

    private Context mContext;
    private List<FoodRecipe> recipes = new ArrayList<>();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView name;
        public TextView like;
        public TextView owner;
        public ImageView profileImage;
        private View.OnClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.popular_image_item);
            name = itemView.findViewById(R.id.popular_name_item);
            like = itemView.findViewById(R.id.popular_like);
            owner = itemView.findViewById(R.id.popular_owner_item);
            profileImage = itemView.findViewById(R.id.profile_image);
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
        FoodRecipe item = recipes.get(i);

        Picasso.get().load(item.getMainImageUrl()).fit().centerCrop().into(myViewHolder.image);
        Picasso.get().load(item.getMainImageUrl()).fit().centerCrop().into(myViewHolder.profileImage); // TODO : change this
        myViewHolder.name.setText(item.getName());
        myViewHolder.like.setText(item.getLike() + " likes");
        myViewHolder.owner.setText("User");

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
}
