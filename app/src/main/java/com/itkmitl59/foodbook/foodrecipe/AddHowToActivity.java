package com.itkmitl59.foodbook.foodrecipe;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

public class AddHowToActivity extends AppCompatActivity {
    private static final String TAG = "Add How To Activity";
    private ImageView image;
    private EditText message;
    private Button addBtn;

    private HowTo mHowTo;
    private Uri imageUri;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_how_to);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mHowTo = new HowTo();
        image = findViewById(R.id.add_how_to_image);
        message = findViewById(R.id.add_how_to_text);
        addBtn = findViewById(R.id.add_how_to_button);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHowTo();
            }
        });
    }

    private void selectImage() {
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
            Picasso.get().load(imageUri).into(image);
            uploadImage();
        }
    }

    private void saveHowTo() {
        mHowTo.setDescription(message.getText().toString());

        log(mHowTo.toString());
        AddFoodRecipeActivity.howTo.add(mHowTo);

        finish();
    }

    private void log(String text) {
        Log.d(TAG, text);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void uploadImage() {

        if (imageUri != null) {
            final String fileName = "how_to_" + System.currentTimeMillis() + "." + getFileExtension(imageUri);
            log("file name : " + fileName);

            StorageReference FolderRef = storage.getReference("HowToImages");  // << folder = "FoodImages"
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
                    log("Upload Fail! " + e.toString());
                    showToast("Upload Fail! " + e.toString());
                }
            });
        } else {
            showToast("Select Image !!");
        }
    }

    private void getImageUrl(String fileName) {
        StorageReference fileUrl = storage.getReference("HowToImages/" + fileName);
        fileUrl.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mHowTo.setImageUrl(uri.toString());
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
