package com.itkmitl59.foodbook.profile;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class viewProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener  {
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private ImageView mProfileImage;
    private int mMaxScrollSize;
    private TextView displayName,aboutText;
    private User curUser;
    private String viewUserUid;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewprofile);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);


        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();



        displayName = (TextView) findViewById(R.id.displayname_text);
        aboutText = (TextView) findViewById(R.id.about_text);


        if(getIntent().getExtras() == null) finish();

        curUser = (User) getIntent().getSerializableExtra("curUser");
        viewUserUid =  getIntent().getStringExtra("viewUid");
        getImageProfile(viewUserUid);

        displayName.setText(curUser.getDisplayName());
        aboutText.setText(curUser.getAboutme());


    }



    private void setupViewPager(ViewPager viewPager) {
        ProfileFragment.Adapter adapter = new ProfileFragment.Adapter(getSupportFragmentManager());

        myfoodsPage page = new myfoodsPage();
        Bundle bundle = new Bundle();
        bundle.putString("otherID", getIntent().getStringExtra("viewUid"));
        page.setArguments(bundle);

        adapter.addFragment(page, "เมนูอาหารที่เขียน");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            mProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    private void getImageProfile(String userUid) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileUrl = storage.getReference("Users/" + userUid);
        StorageReference fileRef = fileUrl.child("profile_img.jpg");
        fileRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().centerCrop().into(mProfileImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
