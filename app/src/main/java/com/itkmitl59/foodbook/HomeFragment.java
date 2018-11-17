package com.itkmitl59.foodbook;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.foodrecipe.DividerItem;
import com.itkmitl59.foodbook.foodrecipe.FoodListActivity;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipeAdapter;
import com.itkmitl59.foodbook.foodrecipe.PoppularRecipeAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "Home_Log";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    private RecyclerView foodList;
    private FoodRecipeAdapter adapter;
    private ArrayList<FoodRecipe> foodRecipes;
    private EditText search;
    private RecyclerView popularList;
    private PoppularRecipeAdapter popularAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView viewAllBtn = getView().findViewById(R.id.view_all_btn);
        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FoodListActivity.class));
            }
        });
        TextView popularSeeAll = getView().findViewById(R.id.view_all_popular);
        popularSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopularIntent();
            }
        });


        ((MainActivity)getActivity()).changeStatusBarColor("#00000000");



        foodRecipes = new ArrayList<>();
        loadDataSetFromFirebase();
        initRecyclerView();

        initPopularList();
        initSearch();


    }

    private void initRecyclerView() {
        foodList = getView().findViewById(R.id.food_list);
        foodList.setNestedScrollingEnabled(false);

        adapter = new FoodRecipeAdapter(foodRecipes, getActivity());

        foodList.setHasFixedSize(true);
        foodList.setAdapter(adapter);

        foodList.setLayoutManager(new LinearLayoutManager(getActivity()));
        foodList.addItemDecoration(new DividerItem(getActivity(), LinearLayoutManager.VERTICAL, 15));
    }

    private void loadDataSetFromFirebase() {
        firestore.collection("FoodRecipes")
                .orderBy("uid", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        foodRecipes.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            FoodRecipe item = document.toObject(FoodRecipe.class);
                            item.setUid(document.getId());
                            foodRecipes.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void initPopularList() {
        popularList = getActivity().findViewById(R.id.popular_item_list);
        popularAdapter = new PoppularRecipeAdapter(getActivity());

        popularList.setHasFixedSize(true);
        popularList.setAdapter(popularAdapter);
        popularList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

    }

    private void initSearch() {
        /*
         *  Reference : https://stackoverflow.com/questions/6529485/how-to-set-edittext-to-show-search-button-or-enter-button-on-keyboard
         * */
        search = getActivity().findViewById(R.id.search_recipe);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    openSearchIntent(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void openSearchIntent(String keyword) {
        Log.d("Main", "Keyword: " + keyword);
        Intent intent = new Intent(getActivity(), FoodListActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }

    private void openPopularIntent() {
        Intent intent = new Intent(getActivity(), FoodListActivity.class);
        intent.putExtra("allPopular", "allPopular");
        startActivity(intent);
    }

}
