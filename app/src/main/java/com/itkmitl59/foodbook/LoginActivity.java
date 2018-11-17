package com.itkmitl59.foodbook;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_login);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        } else {
            loginState();
        }


        final EditText emailLogin = (EditText) findViewById(R.id.inp_email);
        final EditText passwordLogin = (EditText) findViewById(R.id.inp_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressBar = (ProgressBar) findViewById(R.id.loadbar);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailLogin.getText().toString();
                String userPass = passwordLogin.getText().toString();
                if (userEmail.isEmpty() || userPass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your informations", Toast.LENGTH_SHORT).show();
                }else{
                    setLoading(true);
                    signIn(userEmail, userPass);
                }
            }
        });

        TextView regisBtn = findViewById(R.id.regis_link);
        regisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    private void setLoading(boolean isLoading) {
        if (isLoading) {
            btnLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void signIn(String email, String password) {
        btnLogin.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Fail : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                loginState();
            }
        });
    }


    private void loginState() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

}
