package com.itkmitl59.foodbook;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itkmitl59.foodbook.category.CategoryFragment;
import com.itkmitl59.foodbook.foodrecipe.AddFoodRecipeActivity;
import com.itkmitl59.foodbook.profile.ProfileFragment;
import com.itkmitl59.foodbook.profile.User;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main_log";
    private boolean doubleBackToExitPressedOnce = false;

    private BottomNavigationView bottomNavigationView;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    public User getCurrentUser() {
        return currentUser;
    }
    public String getUserImgUrl() {
        return userImgUrl;
    }

    private User currentUser;
    private String userImgUrl;


    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        if(!haveCurrentUser()){
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }


        getUserData();

        setContentView(R.layout.activity_main);
        showFragment(new HomeFragment());
        initBottomNavbar();


    }

    @Override
    protected void onResume() {
        Log.d(TAG,"RESUMMM");
        getUserData();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_view);
        if(f instanceof HomeFragment) bottomNavigationView.setSelectedItemId(R.id.menu_home);
        if(f instanceof CategoryFragment) bottomNavigationView.setSelectedItemId(R.id.menu_categoies);
        if(f instanceof ProfileFragment) bottomNavigationView.setSelectedItemId(R.id.menu_profile);


        super.onResume();
    }

    public void reloadFragment(){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_view);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_view);
        if(f instanceof HomeFragment) {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "กดอีกครั้งเพื่อออก", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

        } else {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
            showFragment(new HomeFragment());
        }
    }


    private void getUserData(){
        mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        currentUser = new User(documentSnapshot.getString("displayName"),
                                mAuth.getCurrentUser().getEmail(),
                                documentSnapshot.getString("phone"),
                                documentSnapshot.getString("aboutme"));
                    }
                });

        StorageReference fileUrl = storage.getReference("Users/"+mAuth.getUid());
        StorageReference fileRef = fileUrl.child("profile_img.jpg");
        fileRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userImgUrl = uri.toString();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userImgUrl = null;
                Log.d(TAG, e.getMessage());
            }
        });

    }





    private void initBottomNavbar(){
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        showFragment(new HomeFragment());
                        return true;
                    case R.id.menu_categoies:
                        showFragment(new CategoryFragment());
                        return true;
                    case R.id.menu_addfood:
                        startActivity(new Intent(getApplicationContext(), AddFoodRecipeActivity.class));
                        return true;
                    case R.id.menu_profile:
                        showFragment(new ProfileFragment());
                        return true;
                }
                return false;
            }
        });
    }

    public void showFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_view, fragment)
                .commit();

    }

    private boolean haveCurrentUser() {
        if(mAuth.getCurrentUser() != null){
            return true;
        }
        return false;
    }




}
