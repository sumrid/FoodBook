package com.itkmitl59.foodbook.profile;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.itkmitl59.foodbook.LoginActivity;
import com.itkmitl59.foodbook.MainActivity;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "Profile_log";

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private ImageView mProfileImage;
    private int mMaxScrollSize;
    private TextView displayName, aboutText;
    private User curUser;
    private String userImgUrl;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        AppBarLayout appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_image);


//        ((MainActivity)getActivity()).changeStatusBarColor("#80d6ff");

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();


        displayName = (TextView) view.findViewById(R.id.displayname_text);
        aboutText = (TextView) view.findViewById(R.id.about_text);


        userImgUrl = ((MainActivity) getActivity()).getUserImgUrl();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.logout_ico);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);

        setDisplayProfile();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    private void setupViewPager(ViewPager viewPager) {

        myfoodsPage page2 = new myfoodsPage();
        Bundle bundle = new Bundle();
        bundle.putBoolean("localSave", true);
        page2.setArguments(bundle);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new myfoodsPage(), "เมนูอาหารที่เขียน");
        adapter.addFragment(page2, "แบบร่างเมนู");
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toLogin);
                return true;
            case R.id.edit_menu:
                Intent intent = new Intent(getActivity(), editProfileActivity.class);
                intent.putExtra("curUser", curUser);
                intent.putExtra("imgUser", userImgUrl);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        setDisplayProfile();
    }

    private void setDisplayProfile() {
        curUser = ((MainActivity) getActivity()).getCurrentUser();

        if (userImgUrl != null)
            Picasso.get().load(userImgUrl).fit().centerCrop().into(mProfileImage);
        displayName.setText(curUser.getDisplayName());
        aboutText.setText(curUser.getAboutme());

        FirebaseStorage storage = FirebaseStorage.getInstance();
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
}
