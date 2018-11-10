package com.itkmitl59.foodbook.foodrecipe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

public class FoodDetailActivity extends AppCompatActivity {
    private ImageView foodImage;
    private TextView foodName;
    private TextView foodDescription;
    private TextView foodIngredients;
    private LinearLayout howToList;

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

        getDataFromFirebase();
    }

    private void log(String text) {
        Log.d("Food Detail", text);
    }

    private void getDataFromFirebase() {
        String id = getIntent().getStringExtra("id");

        firestore.collection("FoodRecipes")
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        log("load Food Recipe");
                        setDisplay(documentSnapshot.toObject(FoodRecipe.class));
                    }
                });
    }

    private void setDisplay(FoodRecipe item) {
        Picasso.get().load(item.getMainImageUrl()).fit().centerCrop().into(foodImage);
        foodName.setText(item.getName());
        foodDescription.setText(item.getDescription());
        foodIngredients.setText(item.getIngredients());
        setDisplayHowTo(item.getHowTos());

        // TODO : Display comment
    }

    private void setDisplayHowTo(List<HowTo> howTo){
        for(HowTo item : howTo){
            View view = getLayoutInflater().inflate(R.layout.how_to_item, null);

            TextView howToView = view.findViewById(R.id.how_to_item);
            TextView howToDescrip = view.findViewById(R.id.how_to_item_descrip);
            ImageView imageView = view.findViewById(R.id.how_to_image);

            howToView.setText(String.format("ขั้นตอน %d/%d", howTo.indexOf(item)+1, howTo.size()));
            howToDescrip.setText(item.getDescription());
            if(item.getImageUrl() != null) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(item.getImageUrl()).fit().centerCrop().into(imageView);
            }
            howToList.addView(view);
        }
    }
}
