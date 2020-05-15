package com.centafrique.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PostBlog extends AppCompatActivity {

    private EditText etNameOfBlog;
    private EditText etDescriptionOfBlog;
    private ImageView imageView;
    private Button btnImage, btnUploadBlog;

    private DatabaseReference mDatabase;

    private Uri imageUri;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private String UserUid;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_blog);

        etNameOfBlog = findViewById(R.id.etNameOfBlog);
        etDescriptionOfBlog = findViewById(R.id.etDescriptionOfBlog);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        imageView = findViewById(R.id.imageView);
        btnImage = findViewById(R.id.btnImage);
        btnUploadBlog = findViewById(R.id.btnUploadBlog);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open Gallery

                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);

            }
        });

        btnUploadBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Save Blog to Database

                final String txtName = etNameOfBlog.getText().toString();
                final String txtDescription = etDescriptionOfBlog.getText().toString();

                if (imageUri != null && !TextUtils.isEmpty(txtName) && !TextUtils.isEmpty(txtDescription)){

                    final String newPostKey = mDatabase.push().getKey();
                    UserUid = mAuth.getCurrentUser().getUid();

                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("Blog saving in progress..");
                    progressDialog.setCanceledOnTouchOutside(false);

                    if (newPostKey != null){

                        progressDialog.show();

                        final DatabaseReference newBlogUserBlogs = mDatabase.child("user_blogs").child(UserUid).child(newPostKey);
                        final DatabaseReference newBlog = mDatabase.child("blogs").child(newPostKey);

                        final StorageReference filePath = mStorageRef.child("blog_images").child(imageUri.getLastPathSegment());
                        UploadTask uploadTask = filePath.putFile(imageUri);
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()){
                                    progressDialog.dismiss();
                                    throw task.getException();
                                }

                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()){

                                    Uri downloadUrl = task.getResult();
                                    String ImageUrl = String.valueOf(downloadUrl);

                                    newBlogUserBlogs.child("blog_name").setValue(txtName);
                                    newBlogUserBlogs.child("blog_description").setValue(txtDescription);
                                    newBlogUserBlogs.child("user_uid").setValue(UserUid);
                                    newBlogUserBlogs.child("image_url").setValue(ImageUrl);

                                    newBlog.child("blog_name").setValue(txtName);
                                    newBlog.child("blog_description").setValue(txtDescription);
                                    newBlog.child("user_uid").setValue(UserUid);
                                    newBlog.child("image_url").setValue(ImageUrl);

                                    Toast.makeText(PostBlog.this, "Blog saved Successfully", Toast.LENGTH_SHORT).show();

                                    progressDialog.dismiss();

                                    startActivity(new Intent(PostBlog.this, MainActivity.class));


                                }

                            }
                        });
                    }else {

                        Toast.makeText(PostBlog.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    if (imageUri == null)
                        Toast.makeText(PostBlog.this, "Please select an image first..", Toast.LENGTH_SHORT).show();
                    if (TextUtils.isEmpty(txtName))
                        etNameOfBlog.setError("Name of blog cannot be empty");
                    if (TextUtils.isEmpty(txtDescription))
                        etDescriptionOfBlog.setError("Description of blog cannot be empty");


                }



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            try {

                imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);

                imageView.setImageBitmap(selectedImage);

            }catch (FileNotFoundException e){

                e.printStackTrace();
                Toast.makeText(this, "Please Try again.", Toast.LENGTH_SHORT).show();
            }

        }else {

            Toast.makeText(this, "You have not selected an image", Toast.LENGTH_SHORT).show();

        }

    }
}
