package com.itkmitl59.foodbook;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.foodrecipe.DividerItem;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipeAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private RecyclerView foodList;
    private FoodRecipeAdapter adapter;
    private ArrayList<FoodRecipe> foodRecipes;

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

}
