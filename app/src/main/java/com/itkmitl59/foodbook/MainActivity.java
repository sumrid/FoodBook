package com.itkmitl59.foodbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.itkmitl59.foodbook.foodrecipe.AddFoodRecipeActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FOR TEST
        auth.signInWithEmailAndPassword("admin@admin.com", "adminadmin");
        startActivity(new Intent(this, AddFoodRecipeActivity.class));
    }
}
