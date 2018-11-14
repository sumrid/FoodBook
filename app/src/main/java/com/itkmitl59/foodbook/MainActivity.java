package com.itkmitl59.foodbook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.itkmitl59.foodbook.category.CategoryFragment;
import com.itkmitl59.foodbook.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        }

        setContentView(R.layout.activity_main);
        showFragment(new HomeFragment());
        initBottomNavbar();


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
