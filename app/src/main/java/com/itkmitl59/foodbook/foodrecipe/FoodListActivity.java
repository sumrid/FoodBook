package com.itkmitl59.foodbook.foodrecipe;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("เมนูอาหารล่าสุด");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        foodRecipes = new ArrayList<>();

        if (getPopular() != null) {            // get popular food
            loadDataSetFromFirebaseOrderByLike();
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("เมนูยอดนิยม");
        } else if (getCategory() != null) {    // get food by category
            getDataByCategory(getCategory());
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("หมวดหมู่ " + getCategory());
        } else if (getKeyword() != null){      // get food by keyword
            getDataByKeyword(getKeyword());
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("ค้นหา  " + getKeyword());
        } else {
            log("load all recipe");
            loadDataSetFromFirebase();
        }

        initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        foodList = findViewById(R.id.food_show_list);
        adapter = new FoodListAdapter(foodRecipes, this);

        foodList.setHasFixedSize(true);
        foodList.setAdapter(adapter);
        foodList.setLayoutManager(new LinearLayoutManager(this));
        foodList.addItemDecoration(new DividerItem(getApplicationContext(), LinearLayoutManager.VERTICAL, 15));
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

    private void loadDataSetFromFirebaseOrderByLike() {
        firestore.collection("FoodRecipes")
                .orderBy("like", Query.Direction.DESCENDING)
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
                .orderBy("uid", Query.Direction.DESCENDING)
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

    private void getDataByCategory(String category) {
        firestore.collection("FoodRecipes")
                .whereEqualTo("category", category)
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

    private String getKeyword() {
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null) log(keyword);
        return keyword;
    }

    private String getCategory() {
        String category = getIntent().getStringExtra("category");
        return category;
    }

    private String getPopular() {
        String category = getIntent().getStringExtra("allPopular");
        return category;
    }
}
