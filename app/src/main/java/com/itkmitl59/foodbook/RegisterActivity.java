package com.itkmitl59.foodbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itkmitl59.foodbook.profile.User;
import com.squareup.picasso.Picasso;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "Register_log";

    private EditText nameReg, emailReg, passwordReg, repasswordReg, phoneReg;
    private TextInputLayout inpLayoutName, inpLayoutEmail, inpLayoutPassword,
            inpLayoutRepass, inpLayoutPhone;

    private ImageView addImgProfile,imgProfile;
    private String nameStr, emailStr, passStr, repassStr, phoneStr,profileImgStr;
    private Uri profileImageUri;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_register);



        nameReg = (EditText) findViewById(R.id.inp_displayname);
        emailReg = (EditText) findViewById(R.id.inp_email);
        passwordReg = (EditText) findViewById(R.id.inp_pass);
        repasswordReg = (EditText) findViewById(R.id.inp_repass);
        phoneReg = (EditText) findViewById(R.id.inp_phone);

        passwordReg.setTransformationMethod(new PasswordTransformationMethod());
        repasswordReg.setTransformationMethod(new PasswordTransformationMethod());

        addImgProfile = (ImageView) findViewById(R.id.add_img_profile);
        imgProfile = (ImageView) findViewById(R.id.img_profile);


        inpLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inpLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inpLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_pass);
        inpLayoutRepass = (TextInputLayout) findViewById(R.id.input_layout_repass);
        inpLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);


        nameReg.addTextChangedListener(new MyTextWatcher(nameReg));
        emailReg.addTextChangedListener(new MyTextWatcher(emailReg));
        passwordReg.addTextChangedListener(new MyTextWatcher(passwordReg));
        repasswordReg.addTextChangedListener(new MyTextWatcher(repasswordReg));
        phoneReg.addTextChangedListener(new MyTextWatcher(phoneReg));


        addImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectImageWindow();
            }
        });


        Button sigupBtn = (Button) findViewById(R.id.register_btn);
        sigupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() || !validateEmail() || !validatePassword() || !validateRePassword()) {
                    return;
                }

                nameStr = nameReg.getText().toString();
                emailStr = emailReg.getText().toString();
                passStr = passwordReg.getText().toString();
                phoneStr = phoneReg.getText().toString();


                User regisUser = new User(nameStr, emailStr, phoneStr, null);

                createAccount(regisUser, passStr);
            }
        });

    }


    private void createAccount(final User user, String password) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                saveUserInfo(mAuth.getUid(),user);
                if(profileImageUri!=null) uploadImage(mAuth.getUid());
                Toast.makeText(getApplicationContext(), "Register Complete", Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("RegisterResult", e.getMessage());
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void saveUserInfo(String userUid, User user){
        firestore.collection("Users").document(userUid).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Save UserInfo Success!!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void uploadImage(String userUid) {
            StorageReference FolderRef = storage.getReference("Users/"+userUid);
            StorageReference fileRef = FolderRef.child("profile_img.jpg");

            fileRef.putFile(profileImageUri) // upload image file
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "Upload Image Success!!");
                            mAuth.signOut();
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
            addImgProfile.setVisibility(View.GONE);
            imgProfile.setVisibility(View.VISIBLE);
            Picasso.get().load(data.getData()).fit().centerCrop().into(imgProfile);
        }
    }



    private boolean validateName() {
        nameStr = nameReg.getText().toString();
        if (nameStr.trim().isEmpty()) {
            inpLayoutName.setError("กรอกชื่อ-นามสกุล");
            requestFocus(nameReg);
            return false;
        } else {
            inpLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        passStr = passwordReg.getText().toString();
        if (passStr.trim().isEmpty()) {
            inpLayoutPassword.setError("กรอกรหัสผ่าน");
            requestFocus(passwordReg);
            return false;
        } else if (passStr.length() < 6) {
            inpLayoutPassword.setError("รหัสผ่านต้องมากกว่า 5 ตัวอักษร");
            requestFocus(passwordReg);
        } else {
            inpLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateRePassword() {
        repassStr = repasswordReg.getText().toString();
        if (repassStr.trim().isEmpty()) {
            inpLayoutRepass.setError("กรอกรหัสผ่านยืนยัน");
            requestFocus(repasswordReg);
            return false;
        } else if (passStr == null || !passStr.equals(repassStr)) {
            inpLayoutRepass.setError("กรอกรหัสผ่านไม่ตรงกัน");
            requestFocus(repasswordReg);
            return false;
        } else {
            inpLayoutRepass.setErrorEnabled(false);
        }
        return true;

    }

    private boolean validateEmail() {
        emailStr = emailReg.getText().toString();
        if (emailStr.isEmpty() || !isValidEmail(emailStr)) {
            inpLayoutEmail.setError("กรอกอีเมล์ไม่ถูกต้อง");
            requestFocus(emailReg);
            return false;
        } else {
            inpLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.inp_displayname:
                    validateName();
                    break;
                case R.id.inp_email:
                    validateEmail();
                    break;
                case R.id.inp_pass:
                    validatePassword();
                    break;
                case R.id.inp_repass:
                    validateRePassword();
                    break;
            }
        }
    }


}
