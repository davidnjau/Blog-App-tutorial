# Project Name
> BlopApp Tutorial

## Table of contents
* [Overview](#Overview)
* [Screenshots](#screenshots)
* [Technologies](#technologies)
* [Features](#features)
* [Contact](#contact)

## Overview
This is a simple blog app aimed at explaining the simple features of developing apps in android.
The app uses the Firebase Authentication for login and registration of users, Firebase Storage for storage of images uploaded by the app and Firebase Database for the database.

## Screenshots
![Example screenshot 1](https://user-images.githubusercontent.com/32579647/82094536-f2103300-9705-11ea-8700-549de5aa98fa.png)


## Technologies
*  version 1.0

## Code Examples
Show examples of usage:

*Fetch Data From Firebase Database
`Query query = FirebaseDatabase.getInstance().getReference().child("blogs");
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
                }).build();`
## Features
* Firebase Authentication
* Firebase Database & Storage
* Firebase Recycler Adapter
## Contact
Created by [@davidnjau](https://davidnjau21.wixsite.com/mysite) 
