package com.centafrique.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin, mRegister;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);

        mLogin = findViewById(R.id.btnLogin);
        mRegister = findViewById(R.id.btnRegisterAccount);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, Register.class));

            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressDialog.setTitle("Please wait..");
                progressDialog.setMessage("User is logging in");
                progressDialog.setCanceledOnTouchOutside(false);

                //Get Texts
                String txtEmail = mEmail.getText().toString();
                String txtPassword = mPassword.getText().toString();

                if (!TextUtils.isEmpty(txtEmail) && !TextUtils.isEmpty(txtPassword)){

                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(Login.this, "Successful Login", Toast.LENGTH_SHORT).show();
                                        //Proceed to MainActivity

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }else {

                                        progressDialog.dismiss();

                                        Toast.makeText(Login.this, "Login error. try again", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                }else {

                    if (TextUtils.isEmpty(txtEmail)) mEmail.setError("Email cannot be empty");
                    if (TextUtils.isEmpty(txtPassword)) mPassword.setError("Password cannot be empty");
                }

            }
        });
    }
}
