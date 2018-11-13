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

import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FoodRecipeAdapter extends RecyclerView.Adapter<FoodRecipeAdapter.ViewHolder>  {
    private ArrayList<FoodRecipe> mFoodRecipes;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView foodImage;
        public TextView foodName, foodDescription,foodOwner, viewCount;
        private ClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.food_image_item);
            foodName = itemView.findViewById(R.id.food_name_item);
            foodDescription = itemView.findViewById(R.id.food_description_item);
            foodOwner = itemView.findViewById(R.id.food_owner_item);
            viewCount = itemView.findViewById(R.id.food_viewcount_item);
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


    public FoodRecipeAdapter(ArrayList<FoodRecipe> dataSet, Context context) {
        this.mFoodRecipes = dataSet;
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final FoodRecipe foodRecipe = mFoodRecipes.get(position);
        Picasso.get().load(foodRecipe.getMainImageUrl()).fit().centerCrop().into(holder.foodImage);
        holder.foodName.setText(foodRecipe.getName());
        holder.foodDescription.setText(foodRecipe.getDescription());
        holder.foodOwner.setText("DisplayName");
        holder.viewCount.setText("views " + foodRecipe.getViews());

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy - HH:mm");
        if(!foodRecipe.getPostDate().isEmpty()){
            try {
                Date date = format.parse(foodRecipe.getPostDate());
                Log.d("Adapter", date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_food_recipe_item, viewGroup, false);
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
}
