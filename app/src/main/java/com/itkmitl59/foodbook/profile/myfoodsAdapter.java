package com.itkmitl59.foodbook.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.comment.Comment;
import com.itkmitl59.foodbook.foodrecipe.AddFoodRecipeActivity;
import com.itkmitl59.foodbook.foodrecipe.FoodDetailActivity;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class myfoodsAdapter extends RecyclerView.Adapter<myfoodsAdapter.ViewHolder>  {
    private ArrayList<FoodRecipe> mFoodRecipes;
    private Context mContext;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
//        if(mAuth.getCurrentUser().getUid().equals(foodRecipe.getOwner()) || foodRecipe.getUid().length() < 5)
//            holder.delete.setVisibility(View.VISIBLE);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foodRecipe.getUid().length() < 5) deleteLocalSave(foodRecipe);
                else deleteOnFirebase(foodRecipe.getUid());
            }
        });

        holder.setOnItemClickListener(new ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(foodRecipe.getUid().length() < 5) startEditIntent(position);    // go to edit intent
                else startIntent(foodRecipe.getUid());               // view food intent
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String key = "recipe_" + auth.getCurrentUser().getUid();

        SharedPreferences preferences = mContext.getSharedPreferences("foodBook", Context.MODE_PRIVATE);
        String json = preferences.getString(key, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<FoodRecipe>>(){}.getType();
            ArrayList<FoodRecipe> dataSet = new Gson().fromJson(json, type);
            int index = -1;

            for(FoodRecipe i : dataSet) {
                if (i.getUid().equals(item.getUid())) index = dataSet.indexOf(i);
            }

            dataSet.remove(index);

            preferences.edit().putString(key, new Gson().toJson(dataSet)).commit();

            mFoodRecipes.clear();
            mFoodRecipes.addAll(dataSet);
            notifyItemRemoved(index);
        }
    }

    private void deleteLocalSave(int index) {
        Log.d("Adapter", "click item " + index);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String key = "recipe_" + auth.getCurrentUser().getUid();

        SharedPreferences preferences = mContext.getSharedPreferences("foodBook", Context.MODE_PRIVATE);
        String json = preferences.getString(key, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<FoodRecipe>>(){}.getType();
            ArrayList<FoodRecipe> dataSet = new Gson().fromJson(json, type);

            dataSet.remove(index);

            preferences.edit().putString(key, new Gson().toJson(dataSet)).commit();

            mFoodRecipes.clear();
            mFoodRecipes.addAll(dataSet);
            notifyItemRemoved(index);
        }
    }

    private void deleteOnFirebase(String uid) {
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        store.collection("FoodRecipes")
                .document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "ลบเรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
