package com.centafrique.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ViewMoreBlogs extends AppCompatActivity {

    private Bundle bundle;
    private String txtBlogUserId;

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more_blogs);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        bundle = getIntent().getExtras();

        if (bundle != null){

            txtBlogUserId = bundle.getString("blog_user_id");


        }else {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }

        FetchDataFromDb();

    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    private void FetchDataFromDb() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("user_blogs")
                .child(txtBlogUserId);

        FirebaseRecyclerOptions<Blogs> options =
                new FirebaseRecyclerOptions.Builder<Blogs>()
                .setQuery(query, new SnapshotParser<Blogs>() {
                    @NonNull
                    @Override
                    public Blogs parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Blogs(

                                Objects.requireNonNull(snapshot.child("blog_name").getValue().toString()),
                                Objects.requireNonNull(snapshot.child("blog_description").getValue().toString()),
                                Objects.requireNonNull(snapshot.child("user_uid").getValue().toString()),
                                Objects.requireNonNull(snapshot.child("image_url").getValue().toString())

                        );

                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<Blogs, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder blogViewHolder, int position, @NonNull Blogs blogs) {

                blogViewHolder.setBlogName(blogs.getBlog_name());
                blogViewHolder.setBlogDescription(blogs.getBlog_description());
                blogViewHolder.setImage(blogs.getImage_url());

                final String blogId = getRef(position).getKey();

                blogViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ViewMoreBlogs.this, ViewBlog.class);
                        intent.putExtra("blog_id", blogId);
                        startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_view, parent, false);


                return new  BlogViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);


    }

    public class BlogViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public TextView tvDescription;
        public ImageView imageView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDescription = itemView.findViewById(R.id.tvBlogDescription);
            tvName = itemView.findViewById(R.id.tvBlogName);
            imageView = itemView.findViewById(R.id.imageView);

        }

        public void setBlogName(String s){

            tvName.setText(s);
        }
        public void setBlogDescription(String s){

            tvDescription.setText(s);
        }

        public void setImage(final String uri){

            Picasso.with(getApplicationContext()).load(uri).into(imageView, new Callback() {
                @Override
                public void onSuccess() {


                }

                @Override
                public void onError() {

                    Picasso.with(getApplicationContext()).load(uri).into(imageView);

                }
            });

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }
}
