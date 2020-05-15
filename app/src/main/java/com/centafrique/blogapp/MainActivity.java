package com.centafrique.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button btnUploadBlog;

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();

        fetchBlogsFromDb();

        btnUploadBlog = findViewById(R.id.btnUploadBlog);
        btnUploadBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start a new Activity

                startActivity(new Intent(MainActivity.this, PostBlog.class));

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void fetchBlogsFromDb() {

        Query query = FirebaseDatabase.getInstance().getReference().child("blogs");
        FirebaseRecyclerOptions<Blogs> options = new FirebaseRecyclerOptions.Builder<Blogs>()
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

                final String blogUid = getRef(position).getKey();

                blogViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this, ViewBlog.class);
                        intent.putExtra("blog_id", blogUid);
                        startActivity(intent);

                    }
                });

            }


            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_view, parent, false);

                return new BlogViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);


    }

    public class BlogViewHolder extends RecyclerView.ViewHolder{

        public TextView tvBlogName;
        public TextView tvBlogDescription;
        public ImageView imageView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tvBlogName = itemView.findViewById(R.id.tvBlogName);
            tvBlogDescription = itemView.findViewById(R.id.tvBlogDescription);

        }

        public void setBlogName(String string){

            tvBlogName.setText(string);

        }

        public void setBlogDescription(String string){

            tvBlogDescription.setText(string);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.item_log_out){

            mAuth.signOut();

            Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
            finish();


        }

        return super.onOptionsItemSelected(item);

    }
}
