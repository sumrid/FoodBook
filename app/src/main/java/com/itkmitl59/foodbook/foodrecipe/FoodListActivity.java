package com.itkmitl59.foodbook.foodrecipe;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.R;

import java.util.ArrayList;

public class FoodListActivity extends AppCompatActivity {
    private static final String TAG = "Food List Activity";

    private RecyclerView foodList;
    private FoodListAdapter adapter;
    private ArrayList<FoodRecipe> foodRecipes;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.foodlist_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("เมนูอาหารล่าสุด");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        foodRecipes = new ArrayList<>();
        if (getKeyword().isEmpty()) {
            log("load all recipe");
            loadDataSetFromFirebase();
        } else {
            getDataByKeyword(getKeyword());
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        foodList = findViewById(R.id.food_show_list);
        adapter = new FoodListAdapter(foodRecipes, this);

        foodList.setHasFixedSize(true);
        foodList.setAdapter(adapter);
        foodList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadDataSetFromFirebase() {
        firestore.collection("FoodRecipes")
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

    private void log(String text) {
        Log.d(TAG, text);
    }

    private void getDataByKeyword(final String keyword) {
        firestore.collection("FoodRecipes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        foodRecipes.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            FoodRecipe item = document.toObject(FoodRecipe.class);
                            item.setUid(document.getId());
                            if (item.getName().contains(keyword)) {
                                foodRecipes.add(item);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private String getKeyword() {
        String keyword = getIntent().getStringExtra("keyword");
        log(keyword);
        return keyword;
    }
}
