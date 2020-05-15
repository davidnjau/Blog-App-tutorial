package com.centafrique.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewBlog extends AppCompatActivity {

    private Bundle bundle;
    private DatabaseReference databaseReference, mDatabaseBlog;
    private String txtName, txtDescription, txtPic;

    private TextView tvBlogName, tvBlogDescription;
    private ImageView imageView;

    private Button btnDeleteBlog, btnViewMoreBlogs;

    private String UserId;
    private String blogUid;

    private FirebaseAuth mAuth;
    private String CurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);

        btnDeleteBlog = findViewById(R.id.btnDeleteBlog);
        btnViewMoreBlogs = findViewById(R.id.btnViewMoreBlogs);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        tvBlogName = findViewById(R.id.tvBlogName);
        tvBlogDescription = findViewById(R.id.tvBlogDescription);

        imageView = findViewById(R.id.imageView);

        bundle = getIntent().getExtras();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user_blogs");

        btnDeleteBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Delete This Particular Blog
                mDatabaseBlog.removeValue();
                databaseReference.child(CurrentUserId).child(blogUid).removeValue();

                Toast.makeText(ViewBlog.this, ""+txtName+" has been deleted successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();



            }
        });

        btnViewMoreBlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ViewBlog.this, ViewMoreBlogs.class);
                intent.putExtra("blog_user_id", UserId);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (bundle != null){

            blogUid = bundle.getString("blog_id");
            if (blogUid != null) {

                mDatabaseBlog = FirebaseDatabase.getInstance().getReference().child("blogs").child(blogUid);

                GetBlogData();
            }


        }else {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void GetBlogData() {

        mDatabaseBlog.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                txtName = String.valueOf(dataSnapshot.child("blog_name").getValue());
                txtDescription = String.valueOf(dataSnapshot.child("blog_description").getValue());
                txtPic = String.valueOf(dataSnapshot.child("image_url").getValue());

                UserId = String.valueOf(dataSnapshot.child("user_uid").getValue());


                if (CurrentUserId.equals(UserId)){

                    btnDeleteBlog.setVisibility(View.VISIBLE);

                }else {

                    btnDeleteBlog.setVisibility(View.GONE);
                }

                tvBlogName.setText(txtName);
                tvBlogDescription.setText(txtDescription);
                Picasso.with(ViewBlog.this).load(txtPic).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
