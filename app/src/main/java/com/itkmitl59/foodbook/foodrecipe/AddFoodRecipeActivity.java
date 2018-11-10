package com.itkmitl59.foodbook.foodrecipe;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddFoodRecipeActivity extends AppCompatActivity {
    private static final String TAG = "Add Food Activity";
    private ImageView foodImage;
    private EditText foodName;
    private EditText foodHowTo;
    private EditText foodIngredients;
    private Button foodAddButton;
    private ProgressBar progressBar;
    private Uri imageUri;
    private int insert_index = 4;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_recipe);

        foodImage = findViewById(R.id.food_image);
        foodName = findViewById(R.id.food_name);
        foodHowTo = findViewById(R.id.food_how_to);
        foodIngredients = findViewById(R.id.food_ingredients);
        foodAddButton = findViewById(R.id.food_add_button);
        progressBar = findViewById(R.id.progressBar);

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectImageWindow();
            }
        });

        foodAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                showLog("Click add button");
//                uploadImage();

                // TODO : this for test
                saveFoodRecipe("http://static.asiawebdirect.com/m/.imaging/678x452/website/bangkok/portals/bangkok-com/homepage/food-top10/allParagraphs/01/top10Set/0/image.jpg");
            }
        });

        Button addHowTo = findViewById(R.id.add_how_to);
        addHowTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEditText();
            }
        });
    }


    /*
     *    Image Upload
     *    Reference : https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-3-image-upload
     */
    private void uploadImage() {

        if (imageUri != null) {
            final String fileName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
            showLog("file name : " + fileName);

            StorageReference FolderRef = storage.getReference("FoodImages");  // << folder = "FoodImages"
            StorageReference fileRef = FolderRef.child(fileName); // << file name

            fileRef.putFile(imageUri) // upload image file
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            getImageUrl(fileName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLog("Upload Fail! " + e.toString());
                    showToast("Upload Fail! " + e.toString());
                    setLoading(false);
                }
            });
        } else {
            showToast("Select Image !!");
            setLoading(false);
        }
    }

    private void getImageUrl(String fileName) {
        StorageReference fileUrl = storage.getReference("FoodImages/" + fileName);
        fileUrl.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveFoodRecipe(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLog("get Url Fail! " + e.toString());
                setLoading(false);
            }
        });
    }

    private void saveFoodRecipe(String imageUrl) {
        // TODO : set all data to food recipe
        String ducumentName = "food_" + System.currentTimeMillis();

        FoodRecipe foodRecipe = new FoodRecipe();
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy - HH:mm");
        foodRecipe.setUid(ducumentName);
        foodRecipe.setName(foodName.getText().toString());
        foodRecipe.setHowTos(getHowTo());
        foodRecipe.setIngredients(foodIngredients.getText().toString());
        foodRecipe.setMainImageUrl(imageUrl);
        foodRecipe.setPostDate(format.format(new Date()));


        firestore.collection("FoodRecipes")
                .document(ducumentName)
                .set(foodRecipe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showLog("Finish Activity");
                        finish();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openSelectImageWindow() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(data.getData()).into(foodImage);
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            foodAddButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            foodAddButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String text) {
        Log.d(TAG, text);
    }

    private void addEditText() {
        // TODO : make it complete

        LinearLayout layout = findViewById(R.id.add_food);

        // add view
        EditText text = new EditText(this);
        text.setText("+ ");
        text.setTag("how_to");
        layout.addView(text, insert_index);
        insert_index++;

        // add layout
//        View.inflate(this, R.layout.how_to_input, layout);
    }

    private boolean isEditText(View view) {
        if (view instanceof EditText && view.getTag() != null && view.getTag().equals("how_to")) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<HowTo> getHowTo() {
        LinearLayout layout = findViewById(R.id.add_food);
        ArrayList<HowTo> howTos = new ArrayList<>();

        for (int i = 0; i < layout.getChildCount() - 1; i++) {
            View view = layout.getChildAt(i);

            if (isEditText(view)) {
                EditText editText = (EditText) layout.getChildAt(i);

                HowTo howTo = new HowTo();
                howTo.setDescription(editText.getText().toString());
                showLog("text " + editText.getText().toString());

                howTos.add(howTo);
            }
        }

        return howTos;
    }
}
