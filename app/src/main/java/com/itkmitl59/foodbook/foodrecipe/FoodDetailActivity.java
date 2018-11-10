package com.itkmitl59.foodbook.foodrecipe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itkmitl59.foodbook.R;
import com.itkmitl59.foodbook.comment.Comment;
import com.itkmitl59.foodbook.comment.CommentAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class FoodDetailActivity extends AppCompatActivity {
    private static final String TAG = "Food Detail Activity";
    private String foodID;

    private ImageView foodImage;
    private TextView foodName;
    private TextView foodDescription;
    private TextView foodIngredients;
    private LinearLayout howToList;
    private RecyclerView commentList;
    private EditText commentMessage;
    private Button postComment;
    private CommentAdapter adapter;

    private List<Comment> comments;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodImage = findViewById(R.id.food_detail_image);
        foodName = findViewById(R.id.food_detail_name);
        foodDescription = findViewById(R.id.food_detail_descrip);
        foodIngredients = findViewById(R.id.food_detail_ingredients);
        howToList = findViewById(R.id.how_to_list);

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

        // TODO : like system
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
                        setDisplay(documentSnapshot.toObject(FoodRecipe.class));
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
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void setDisplay(FoodRecipe item) {
        Picasso.get().load(item.getMainImageUrl()).fit().centerCrop().into(foodImage);
        foodName.setText(item.getName());
        foodDescription.setText(item.getDescription());
        foodIngredients.setText(item.getIngredients());
        setDisplayHowTo(item.getHowTos());
    }

    private void setDisplayHowTo(List<HowTo> howTo) {
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
            comment.setUserID("xxxx user xxxx");
            commentMessage.setText("...");
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

}
