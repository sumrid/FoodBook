package com.itkmitl59.foodbook.foodrecipe;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itkmitl59.foodbook.MainActivity;
import com.itkmitl59.foodbook.R;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddFoodRecipeActivity extends AppCompatActivity {
    private static final String TAG = "Add Food Activity";
    private ImageView foodImage;
    private EditText foodName;
    private EditText foodDescription;
    private EditText foodIngredients;
    private EditText foodCategory;
    private Button foodAddButton;
    private ProgressBar progressBar;
    private Uri imageUri;

    private FoodRecipe mFoodRecipe;
    static List<HowTo> howTo;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_recipe);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("เพิ่มเมนูอาหาร");
        }

        howTo = new ArrayList<>();

        foodImage = findViewById(R.id.food_image);
        foodName = findViewById(R.id.food_name);
        foodIngredients = findViewById(R.id.food_ingredients);
        foodAddButton = findViewById(R.id.food_add_button);
        foodCategory = findViewById(R.id.food_category);
        foodDescription = findViewById(R.id.food_description);
        progressBar = findViewById(R.id.progressBar);

        foodCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSelect();
            }
        });
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
                uploadImage();
            }
        });

        Button addHowTo = findViewById(R.id.add_how_to);
        addHowTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHowTo();
            }
        });

        loadLocalData();
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
        foodRecipe.setHowTos(howTo);
        foodRecipe.setIngredients(foodIngredients.getText().toString());
        foodRecipe.setMainImageUrl(imageUrl);
        foodRecipe.setPostDate(format.format(new Date()));
        foodRecipe.setCategory(foodCategory.getText().toString());
        foodRecipe.setOwner(FirebaseAuth.getInstance().getCurrentUser().getUid());
        foodRecipe.setDescription(foodDescription.getText().toString());

        firestore.collection("FoodRecipes")
                .document(ducumentName)
                .set(foodRecipe)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showLog("Finish Activity");
                        showToast("บันทึกสำเร็จ");
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
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(foodImage);
            showLog(data.getData().getScheme()+" type");

            final int takeFlags = getIntent().getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
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

    private void addHowTo() {
        Intent intent = new Intent(this, AddHowToActivity.class);
        startActivity(intent);
    }

    private void setDisplayHowTo(List<HowTo> howTo) {
        showLog("method setDisplayHowTo");

        LinearLayout howToList = findViewById(R.id.how_to_list);
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

    @Override
    protected void onResume() {
        super.onResume();
        setDisplayHowTo(howTo);
    }

    private void showDialogSelect() {
        // TODO : It's not complete
        final String category[] = {"ผัด", "ทอด", "นึ่ง", "ยำ"};

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setItems(category, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = category[which];
                foodCategory.setText(selected);
            }
        });
        builder.setNegativeButton("ยกเลิก", null);
        builder.create();

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showSaveDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ต้องการบันทึกไว้ก่อนหรือไม่ ?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("บันทึก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                localSave();
                showToast("บันทึกเรียบร้อย");
                finish();
            }
        });
        builder.setNegativeButton("ไม่บันทึก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }

    private void localSave() {
        if (Hawk.isBuilt() == false) Hawk.init(this).build();
        boolean saved = false;

        List<FoodRecipe> foodRecipes = new ArrayList<>();

        // get data
        if (Hawk.get("recipe") != null) {
            ArrayList<FoodRecipe> dataSet = Hawk.get("recipe");
            foodRecipes.addAll(dataSet);
        }

        // check if already saved
        if (mFoodRecipe != null) {
            for (FoodRecipe item : foodRecipes) {
                if (item.getUid().equals(mFoodRecipe.getUid())) saved = true;
            }
        } else {
            mFoodRecipe = new FoodRecipe();
        }

        if (!saved) { // if it first save..  then set ID for save. (int index)
            mFoodRecipe.setUid(foodRecipes.size() + "");
        }

        if(imageUri != null)mFoodRecipe.setMainImageUrl(imageUri.toString());
        mFoodRecipe.setName(foodName.getText().toString());
        mFoodRecipe.setDescription(foodDescription.getText().toString());
        mFoodRecipe.setIngredients(foodIngredients.getText().toString());
        mFoodRecipe.setHowTos(howTo);
        mFoodRecipe.setCategory(foodCategory.getText().toString());
        showLog("save " + howTo);

        // update
        if (saved) {
            int index = Integer.parseInt(mFoodRecipe.getUid());
            foodRecipes.set(index, mFoodRecipe);
        } else {
            foodRecipes.add(mFoodRecipe);
        }

        // save
        Hawk.put("recipe", foodRecipes);
    }

    private void loadLocalData() {
        mFoodRecipe = (FoodRecipe) getIntent().getSerializableExtra("recipe");

        if (mFoodRecipe != null) {
            showLog("local id " + mFoodRecipe.getUid());
            showLog("local url " + mFoodRecipe.getMainImageUrl());
            showLog("local howto " + mFoodRecipe.getHowTos());

            if (mFoodRecipe.getMainImageUrl() != null) {
                Uri uri = Uri.parse(mFoodRecipe.getMainImageUrl());
                Picasso.get().load(uri).into(foodImage);
                Picasso.get().load(mFoodRecipe.getMainImageUrl()).fit().centerCrop().into(foodImage);
            }
            foodName.setText(mFoodRecipe.getName());
            foodDescription.setText(mFoodRecipe.getDescription());
            foodIngredients.setText(mFoodRecipe.getIngredients());
            foodCategory.setText(mFoodRecipe.getCategory());
            howTo.addAll(mFoodRecipe.getHowTos());
            setDisplayHowTo(howTo);
        }
    }

    @Override
    public void onBackPressed() {
        showSaveDialog();
    }
}
