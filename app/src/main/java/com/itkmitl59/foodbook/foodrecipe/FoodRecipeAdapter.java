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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FoodRecipeAdapter extends RecyclerView.Adapter<FoodRecipeAdapter.ViewHolder>  {
    private ArrayList<FoodRecipe> mFoodRecipes;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView foodImage;
        public TextView foodName, foodDescription,foodOwner, viewCount, postTime;
        private ClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.food_image_item);
            foodName = itemView.findViewById(R.id.food_name_item);
            foodDescription = itemView.findViewById(R.id.food_description_item);
            foodOwner = itemView.findViewById(R.id.food_owner_item);
            viewCount = itemView.findViewById(R.id.food_viewcount_item);
            postTime = itemView.findViewById(R.id.food_posttime_item);
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
                holder.postTime.setText(calculateTime(date));
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

    private String calculateTime(Date date) {
        Date currentTime = new Date();
        DateTime d1 = new DateTime(currentTime);
        DateTime d2 = new DateTime(date);

        int hour = Hours.hoursBetween(d2,d1).getHours();
        int day = Days.daysBetween(d2,d1).getDays();
        Log.d("Adapter", hour+" hours diff" + "days diff " + day);

        if(hour > 24) {
            return day + "วันที่แล้ว";
        } else {
            return hour + "ชั่วโมงที่แล้ว";
        }
    }

    private void setDisplayUser(final ViewHolder holder, FoodRecipe item) {
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        store.collection("users")
                .document(item.getOwner())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.foodOwner.setText("set text");
            }
        });

    }
}
