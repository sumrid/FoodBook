package com.itkmitl59.foodbook.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.foodrecipe.DividerItem;
import com.itkmitl59.foodbook.foodrecipe.FoodRecipe;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class myfoodsPage extends Fragment {
    private static final String TAG = "myFoods_log";

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private RecyclerView myfoodList;
    private myfoodsAdapter adapter;
    private ArrayList<FoodRecipe> foodRecipes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_myfoods, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        foodRecipes = new ArrayList<>();
        initRecyclerView();
        if (isForLocalSave()) {
            getLocalData();
        } else {
            loadDataSetFromFirebase();
        }
    }


    private void initRecyclerView() {
        myfoodList = getView().findViewById(R.id.myfoods_list);
        adapter = new myfoodsAdapter(foodRecipes, getActivity());
        myfoodList.setHasFixedSize(true);
        myfoodList.setAdapter(adapter);
        myfoodList.setLayoutManager(new LinearLayoutManager(getActivity()));
        myfoodList.addItemDecoration(new DividerItem(getActivity(), LinearLayoutManager.VERTICAL, 15));

    }

    private void loadDataSetFromFirebase() {
        Query query = firestore.collection("FoodRecipes");

        if (getBundle() != null) {
            Log.d("Food page", getBundle().getString("otherID"));
            query = query.whereEqualTo("owner", getBundle().getString("otherID"));   // find other food recipe
        } else {
            query = query.whereEqualTo("owner", auth.getCurrentUser().getUid());   // find my food recipe
        }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private boolean isForLocalSave() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.get("localSave") != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void getLocalData() {
        if (Hawk.isBuilt() == false) Hawk.init(getContext()).build();

        if (Hawk.get("recipe") != null) {
            ArrayList<FoodRecipe> dataSet = Hawk.get("recipe");
            foodRecipes.clear();
            foodRecipes.addAll(dataSet);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isForLocalSave()) {
            getLocalData();
        } else {
            loadDataSetFromFirebase();
        }
    }

    private Bundle getBundle() {
        Bundle bundle = getArguments();
        return bundle;
    }
}
