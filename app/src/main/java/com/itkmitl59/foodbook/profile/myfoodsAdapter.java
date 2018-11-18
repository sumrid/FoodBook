package com.itkmitl59.foodbook.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.comment.Comment;
import com.itkmitl59.foodbook.foodrecipe.AddFoodRecipeActivity;
import com.itkmitl59.foodbook.foodrecipe.FoodDetailActivity;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class myfoodsAdapter extends RecyclerView.Adapter<myfoodsAdapter.ViewHolder>  {
    private ArrayList<FoodRecipe> mFoodRecipes;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView foodImage;
        public TextView foodName, foodDescription;
        public TextView likeCount, commentCount, viewCount;
        public ImageButton delete;
        private ClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.food_image_item);
            foodName = itemView.findViewById(R.id.food_name_item);
            foodDescription = itemView.findViewById(R.id.food_description_item);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            viewCount = itemView.findViewById(R.id.view_count);
            delete = itemView.findViewById(R.id.delete_btn);
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


    public myfoodsAdapter(ArrayList<FoodRecipe> dataSet, Context context) {
        this.mFoodRecipes = dataSet;
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FoodRecipe foodRecipe = mFoodRecipes.get(position);
        Picasso.get().load(foodRecipe.getMainImageUrl()).fit().centerCrop().placeholder(R.drawable.add_img).into(holder.foodImage);
        holder.foodName.setText(foodRecipe.getName());
        holder.foodDescription.setText(foodRecipe.getDescription());
        holder.viewCount.setText("" + foodRecipe.getViews());
        holder.likeCount.setText("" + foodRecipe.getLike());
        getCommentCount(holder.commentCount, foodRecipe.getUid());

        if (foodRecipe.getUid().length() < 5) holder.delete.setVisibility(View.VISIBLE);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocalSave(foodRecipe);
            }
        });

        holder.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(foodRecipe.getUid().length() < 5) startEditIntent(position);
                else startIntent(foodRecipe.getUid());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_myfoods_item, viewGroup, false);
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

    private void getCommentCount(final TextView editText, String foodID){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("comments")
                .whereEqualTo("foodID", foodID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        int count = queryDocumentSnapshots.size();
                        editText.setText("" + count);
                    }
                });
    }

    private void startEditIntent(int position) {
        Intent intent = new Intent(mContext, AddFoodRecipeActivity.class);
        intent.putExtra("recipe", mFoodRecipes.get(position));
        mContext.startActivity(intent);
    }

    private void deleteLocalSave(FoodRecipe item) {
        Log.d("Adapter", "click item " + item.getUid());

        if (Hawk.isBuilt() == false) Hawk.init(mContext).build();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String key = "recipe_" + auth.getCurrentUser().getUid();

        if( Hawk.get(key) != null) {
            ArrayList<FoodRecipe> items = Hawk.get(key);
            int index = -1;

            for(FoodRecipe recipe : items) {
                if (recipe.getUid().equals(item.getUid())) index = items.indexOf(recipe);
            }

            items.remove(index);

            Hawk.put(key, items);

            mFoodRecipes.clear();
            mFoodRecipes.addAll(items);
            notifyItemRemoved(index);
        }
    }
}
