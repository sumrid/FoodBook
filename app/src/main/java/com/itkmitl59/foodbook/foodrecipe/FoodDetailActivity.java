package com.itkmitl59.foodbook.foodrecipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itkmitl59.foodbook.Like;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.comment.Comment;
import com.itkmitl59.foodbook.comment.CommentAdapter;
import com.itkmitl59.foodbook.profile.User;
import com.itkmitl59.foodbook.profile.viewProfileActivity;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class FoodDetailActivity extends AppCompatActivity {
    private static final String TAG = "Food Detail Activity";
    private String foodID;
    private FoodRecipe mFoodRecipe;
    private boolean isUpdatedView = false;

    private ImageView foodImage;
    private TextView foodName;
    private TextView foodDescription;
    private TextView foodIngredients;
    private LinearLayout howToList,link_profile;
    private RecyclerView commentList;
    private EditText commentMessage;
    private Button postComment;
    private LikeButton likeButton;
    private TextView likeCount;
    private TextView commentCount,commentCount2;
    private TextView viewCount;
    private TextView ownerName;
    private CommentAdapter adapter;
    private ImageView ownerImg;

    private List<Comment> comments;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.food_detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        foodImage = findViewById(R.id.food_detail_image);
        foodName = findViewById(R.id.food_detail_name);
        foodDescription = findViewById(R.id.food_detail_descrip);
        foodIngredients = findViewById(R.id.food_detail_ingredients);
        howToList = findViewById(R.id.how_to_list);
        commentCount = findViewById(R.id.comment_count);
        commentCount2 = findViewById(R.id.comment_text);
        viewCount = findViewById(R.id.view_count);

        ownerName = findViewById(R.id.food_owner_name);

        link_profile = findViewById(R.id.link_profile);
        ownerImg = findViewById(R.id.food_owner_image);

        comments = new ArrayList<>();
        commentMessage = findViewById(R.id.comment_message_input);
        commentList = findViewById(R.id.comment_list);
        commentList.setHasFixedSize(true);
        adapter = new CommentAdapter(comments, this);
        commentList.setAdapter(adapter);
        commentList.setLayoutManager(new LinearLayoutManager(this));

        postComment = findViewById(R.id.post_comment_btn);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        getDataFromFirebase();
        toolbarShowTitle();

        likeCount = findViewById(R.id.like_count);
        likeButton = findViewById(R.id.like_button);
        checkUserLiked();
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                log("like button - " + likeButton.isLiked());
                manageLike(likeButton.isLiked());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                log("like button - " + likeButton.isLiked());
                manageLike(likeButton.isLiked());
            }
        });


        link_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void log(String text) {
        Log.d(TAG, text);
    }

    private void getDataFromFirebase() {
        foodID = getIntent().getStringExtra("id");

        // get FoodRecipe Object
        firestore.collection("FoodRecipes")
                .document(foodID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        log("load Food Recipe");
                        mFoodRecipe = documentSnapshot.toObject(FoodRecipe.class);
                        setDisplay(mFoodRecipe);
                        updateView();
                    }
                });

        // get Comments of this recipe
        firestore.collection("comments")
                .whereEqualTo("foodID", foodID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        comments.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            comments.add(document.toObject(Comment.class));
                        }
                        commentCount.setText("" + comments.size());
                        commentCount2.setText(comments.size()+" ความคิดเห็น");
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    private void toolbarShowTitle() {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(foodName.getText());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void setDisplay(FoodRecipe item) {
        Picasso.get().load(item.getMainImageUrl()).fit().centerCrop().into(foodImage);
        foodName.setText(item.getName());
        foodDescription.setText(item.getDescription());
        foodIngredients.setText(item.getIngredients());
        getOwnerDisplay(item.getOwner());
        setDisplayHowTo(item.getHowTos());
        likeCount.setText("" + item.getLike());
        viewCount.setText("เข้าชม " + item.getViews());
    }

    private void setDisplayHowTo(List<HowTo> howTo) {
        howToList.removeAllViews();
        for (HowTo item : howTo) {
            View view = getLayoutInflater().inflate(R.layout.how_to_item, null);

            TextView howToView = view.findViewById(R.id.how_to_item);
            TextView howToDescrip = view.findViewById(R.id.how_to_item_descrip);
            ImageView imageView = view.findViewById(R.id.how_to_image);

            howToView.setText(String.format("ขั้นตอน %d/%d", howTo.indexOf(item) + 1, howTo.size()));
            howToDescrip.setText(item.getDescription());
            if (item.getImageUrl() != null) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(item.getImageUrl()).fit().centerCrop().into(imageView);
            }
            howToList.addView(view);
        }
    }

    private void postComment() {
        String message = commentMessage.getText().toString();
        if (!message.isEmpty()) {
            log("have comment message");

            Comment comment = new Comment();
            comment.setDate(new Date());
            comment.setMessage(message);
            comment.setFoodID(foodID);
            comment.setUserID(auth.getCurrentUser().getUid());
            commentMessage.setText("");
            saveComment(comment);
        } else {
            log("message is empty");
        }
    }

    private void saveComment(Comment comment) {
        String documentName = "comment_" + System.currentTimeMillis();
        firestore.collection("comments")
                .document(documentName)
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        log("comment saved");
                    }
                });
    }

    private void manageLike(boolean isLiked) {
        int num = 0;
        if (isLiked) {
            num = 1;
            Like like = new Like(foodID, auth.getCurrentUser().getUid());
            firestore.collection("liked")
                    .document(foodID + "_" + auth.getCurrentUser().getUid())
                    .set(like);
        } else {
            num = -1;
            String documentName = foodID + "_" + auth.getCurrentUser().getUid();
            firestore.collection("liked")
                    .document(documentName)
                    .delete();
        }

        // update like count
        firestore.collection("FoodRecipes")
                .document(foodID)
                .update("like", num + mFoodRecipe.getLike());
    }

    private void checkUserLiked() {
        log("check user liked");
        firestore.collection("liked")
                .whereEqualTo("foodID", foodID)
                .whereEqualTo("userID", auth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots.size() > 0) {
                            log("user liked");
                            likeButton.setLiked(true);
                        }
                    }
                });
    }

    private void updateView() {
        if (!isUpdatedView) {
            firestore.collection("FoodRecipes")
                    .document(foodID)
                    .update("views", mFoodRecipe.getViews() + 1);
            isUpdatedView = true;
        }
    }

    private void getOwnerDisplay(String userUid){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileUrl = storage.getReference("Users/"+userUid);
        StorageReference fileRef = fileUrl.child("profile_img.jpg");
        fileRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).fit().centerCrop().into(ownerImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        firestore.collection("Users").document(userUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ownerName.setText(documentSnapshot.getString("displayName"));
                }
            }
        });
    }
}
