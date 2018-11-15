package com.itkmitl59.foodbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        }



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
                        Log.d(TAG,uri.toString());
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

    private void showFragment(Fragment fragment){
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
