package com.itkmitl59.foodbook.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itkmitl59.foodbook.MainActivity;
import com.itkmitl59.foodbook.R;
import com.squareup.picasso.Picasso;

public class editProfileActivity extends AppCompatActivity {
    private static final String TAG = "editProfile_log";

    private User curUser;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private String oldPassStr,newPassStr,passStr,userImgUrl;
    private ImageView user_img;
    private Uri profileImageUri;
    private TextView user_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("แก้ไขข้อมูลส่วนตัว");
        }


        final TextView user_displayname = (TextView) findViewById(R.id.inp_displayname);
        final EditText user_email = (EditText) findViewById(R.id.inp_email);
        final EditText user_phone = (EditText) findViewById(R.id.inp_phone);
        final EditText user_about = (EditText) findViewById(R.id.inp_about);
        user_img = (ImageView) findViewById(R.id.profile_image);
        user_pass = (TextView) findViewById(R.id.inp_pass);
        final Button btn_save = (Button) findViewById(R.id.btn_save);

        curUser = (User) getIntent().getSerializableExtra("curUser");
        userImgUrl = getIntent().getStringExtra("imgUser");
        Picasso.get().load(userImgUrl).fit().centerCrop().into(user_img);

        final RelativeLayout profile_img = (RelativeLayout) findViewById(R.id.layout_img_profile);


        user_displayname.setText(curUser.getDisplayName());
        user_email.setText(curUser.getEmail());
        user_phone.setText(curUser.getPhone());
        user_about.setText(curUser.getAboutme());

        user_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectImageWindow();
            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = user_displayname.getText().toString();
                String emailStr = user_email.getText().toString();
                passStr = user_pass.getText().toString();
                String aboutStr = user_about.getText().toString();
                String phoneStr = user_phone.getText().toString();


                if (nameStr.isEmpty() || phoneStr.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter your information", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Working...", Toast.LENGTH_LONG).show();
                    final User user = new User(nameStr, emailStr, phoneStr,aboutStr);

                    //if not input newPassword
                    if(passStr.isEmpty()){
                        updateUser(user);
                        return;
                    }
                    firebaseUser = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), oldPassStr);
                    firebaseUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firebaseUser.updatePassword(passStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                        updateUser(user);
                                    } else {
                                        Log.d(TAG, "Password update Error!!");
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"กรอกรหัสผ่านเก่าไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        });

    }

    private void updateUser(User user){
        if(profileImageUri!=null) uploadImage(mAuth.getCurrentUser().getUid());
        mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Saved Changes", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadImage(String userUid) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference FolderRef = storage.getReference("Users/"+userUid);
        StorageReference fileRef = FolderRef.child("profile_img.jpg");

        fileRef.putFile(profileImageUri) // upload image file
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Upload Image Success!!");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
            }
        });

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
            profileImageUri = data.getData();
            Picasso.get().load(data.getData()).fit().centerCrop().into(user_img);
        }
    }

    private void showConfirmDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.confirm_pass_dialogbox, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText oldPassText = (EditText) mView.findViewById(R.id.inp_oldpass);
        final EditText newPassText = (EditText) mView.findViewById(R.id.inp_newpass);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });


        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newPassText.length() < 6){
                    Toast.makeText(getApplicationContext(), "รหัสผ่านใหม่ต้องยาวมากกว่า 5 ตัวอักษร", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    oldPassStr = oldPassText.getText().toString();
                    user_pass.setText(newPassText.getText().toString());
                    alertDialogAndroid.dismiss();
                }
            }
        });
    }


}
