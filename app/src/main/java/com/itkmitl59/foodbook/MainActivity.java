package com.itkmitl59.foodbook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.foodrecipe.DividerItem;
import com.itkmitl59.foodbook.foodrecipe.FoodListActivity;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipeAdapter;
import com.itkmitl59.foodbook.foodrecipe.PoppularRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private RecyclerView foodList;
    private FoodRecipeAdapter adapter;
    private ArrayList<FoodRecipe> foodRecipes;
    private EditText search;
    private RecyclerView popularList;
    private PoppularRecipeAdapter popularAdapter;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }

        setContentView(R.layout.activity_main);

        // FOR TEST
        auth.signInWithEmailAndPassword("a@a.com", "12341234");
//        startActivity(new Intent(this, FoodListActivity.class));

        foodRecipes = new ArrayList<>();
        loadDataSetFromFirebase();
        initRecyclerView();

        initPopularList();
        initSearch();
    }


    private void initRecyclerView() {
        foodList = findViewById(R.id.food_list);
        adapter = new FoodRecipeAdapter(foodRecipes, this);

        foodList.setHasFixedSize(true);
        foodList.setAdapter(adapter);
        foodList.setLayoutManager(new LinearLayoutManager(this));
        foodList.addItemDecoration(new DividerItem(getApplicationContext(), LinearLayoutManager.VERTICAL, 15));
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

    private void initPopularList() {
        popularList = findViewById(R.id.popular_item_list);
        popularAdapter = new PoppularRecipeAdapter(this);

        popularList.setHasFixedSize(true);
        popularList.setAdapter(popularAdapter);
        popularList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    private void initSearch() {
        /*
        *  Reference : https://stackoverflow.com/questions/6529485/how-to-set-edittext-to-show-search-button-or-enter-button-on-keyboard
        * */
        search = findViewById(R.id.search_recipe);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    openIntent(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void openIntent(String keyword) {
        Log.d("Main", keyword);
        Intent intent = new Intent(this, FoodListActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }
}
