package com.itkmitl59.foodbook.category;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.foodrecipe.FoodListActivity;
import com.itkmitl59.foodbook.foodrecipe.FoodListAdapter;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<Category> mCategory;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView cateImage;
        public TextView cateName;
        private FoodListAdapter.ClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cateImage = itemView.findViewById(R.id.category_image_item);
            cateName = itemView.findViewById(R.id.category_name_item);
            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(FoodListAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }


    public CategoryAdapter(ArrayList<Category> dataSet, Context context) {
        this.mCategory = dataSet;
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Category category = mCategory.get(position);
        Picasso.get().load(category.getImageUrl()).fit().centerCrop().into(holder.cateImage);
        holder.cateName.setText(category.getName());

        holder.setOnItemClickListener(new FoodListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                openIntent(category.getName());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_category_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    private void openIntent(String category) {
        Intent intent = new Intent(mContext, FoodListActivity.class);
        intent.putExtra("category", category);
        mContext.startActivity(intent);
    }
}
