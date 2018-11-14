package com.itkmitl59.foodbook.category;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.foodrecipe.DividerItem;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipeAdapter;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private RecyclerView cateList;
    private CategoryAdapter adapter;
    private ArrayList<Category> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        categories = new ArrayList<>();
        categories.clear();
        categories.add(new Category("ต้ม",R.drawable.category1_img));
        categories.add(new Category("ทอด",R.drawable.category2_img));
        categories.add(new Category("นึ่ง",R.drawable.category3_img));
        categories.add(new Category("ผัด",R.drawable.category4_img));
        categories.add(new Category("แกง",R.drawable.category5_img));
        categories.add(new Category("ย่าง",R.drawable.category6_img));

        initRecyclerView();
    }


    private void initRecyclerView() {
        cateList = getView().findViewById(R.id.category_list);
        adapter = new CategoryAdapter(categories, getActivity());

        cateList.setHasFixedSize(true);
        cateList.setAdapter(adapter);
        cateList.setLayoutManager(new GridLayoutManager(getActivity(),2));
    }
}
