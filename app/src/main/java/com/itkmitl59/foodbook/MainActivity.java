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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itkmitl59.foodbook.category.CategoryFragment;
import com.itkmitl59.foodbook.foodrecipe.AddFoodRecipeActivity;
import com.itkmitl59.foodbook.profile.ProfileFragment;
import com.itkmitl59.foodbook.profile.User;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main_log";
    private boolean doubleBackToExitPressedOnce = false;


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

        super.onResume();
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
            super.onBackPressed();
        }
    }


    private void getUserData(){
        mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    currentUser = new User(documentSnapshot.getString("displayName"),
                            mAuth.getCurrentUser().getEmail(),
                            documentSnapshot.getString("phone"),
                            documentSnapshot.getString("aboutme"));
                }
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
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
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
                .addToBackStack(null)
                .commit();

    }

    private boolean haveCurrentUser() {
        if(mAuth.getCurrentUser() != null){
            return true;
        }
        return false;
    }




}
