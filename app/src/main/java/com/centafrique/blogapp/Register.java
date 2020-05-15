package com.centafrique.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText mUserName, mEmailAddress, mPassword, mConfirmPassword;
    private Button mRegister, mLogin;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        mUserName = findViewById(R.id.etUsername);
        mEmailAddress = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mConfirmPassword = findViewById(R.id.etConfirmPassword);

        mRegister = findViewById(R.id.btnRegisterAccount);
        mLogin = findViewById(R.id.btnSignIn);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Register.this, Login.class));

                //Create a login page

            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //GetText from the edittext
                final String txtUserName = mUserName.getText().toString().trim();
                final String txtEmailAddress = mEmailAddress.getText().toString().trim();
                final String txtPassword = mPassword.getText().toString().trim();
                String txtConfirmPassword = mConfirmPassword.getText().toString().trim();

                //Check if the above texts are empty
                if (!TextUtils.isEmpty(txtUserName) &&
                        !TextUtils.isEmpty(txtEmailAddress) &&
                        !TextUtils.isEmpty(txtPassword) &&
                        !TextUtils.isEmpty(txtConfirmPassword)){

                    progressDialog.setTitle("Please wait..");
                    progressDialog.setMessage("Registration ongoing..");
                    progressDialog.setCanceledOnTouchOutside(false);

                    //All Fields have been filled we can proceed to registering user
                    //Check if the password and the confirm password are the same
                    if (txtPassword.equals(txtConfirmPassword)){

                        progressDialog.show();
                        //The passwords are the same

                        mAuth.createUserWithEmailAndPassword(txtEmailAddress, txtPassword)
                                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()){
                                            //The registration is successfull, Go to main Activity
                                            Toast.makeText(Register.this, "Successful Login.", Toast.LENGTH_SHORT).show();

                                            String firebaseUser = mAuth.getCurrentUser().getUid();

                                            //A new child
                                            DatabaseReference newUser = databaseReference.child("user_details").child(firebaseUser);
                                            newUser.child("user_name").setValue(txtUserName);
                                            newUser.child("email").setValue(txtEmailAddress);
                                            newUser.child("uid").setValue(firebaseUser);

                                            progressDialog.dismiss();

                                            Intent intent = new Intent(Register.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();


                                        }else {
                                            //The registration failed, Show toast message

                                            progressDialog.dismiss();

                                            Toast.makeText(Register.this, "Authentication error. Please try again", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                });


                    }else {

                        //Do something if passwords are not the same & Clear both fields

                        mPassword.setText("");
                        mConfirmPassword.setText("");
                        Toast.makeText(Register.this, "Passwords must be the same", Toast.LENGTH_SHORT).show();

                    }

                }else {
                    //If a field has not been filled show error text
                    //Check which field is not filled

                    if (TextUtils.isEmpty(txtUserName))mUserName.setError("Username Cannot be empty");
                    if (TextUtils.isEmpty(txtEmailAddress))mEmailAddress.setError("Email Address Cannot be empty");
                    if (TextUtils.isEmpty(txtPassword))mPassword.setError("Password Cannot be empty");
                    if (TextUtils.isEmpty(txtConfirmPassword))mConfirmPassword.setError("Password Cannot be empty");

                }

            }
        });
    }
}
