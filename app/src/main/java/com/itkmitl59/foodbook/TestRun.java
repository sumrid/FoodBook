package com.itkmitl59.foodbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.itkmitl59.foodbook.profile.ProfileFragment;

public class TestRun extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_main);

        if(savedInstanceState==null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_view, new ProfileFragment())
                    .commit();
        }

    }

}
