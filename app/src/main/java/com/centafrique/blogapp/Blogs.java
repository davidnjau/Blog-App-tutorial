package com.centafrique.blogapp;

public class Blogs {

    private String blog_name;
    private String blog_description;
    private String user_uid;
    private String image_url;

    public Blogs(String blog_name, String blog_description, String user_uid, String image_url) {
        this.blog_name = blog_name;
        this.blog_description = blog_description;
        this.user_uid = user_uid;
        this.image_url = image_url;
    }

    public Blogs() {
    }

    public String getBlog_name() {
        return blog_name;
    }

    public void setBlog_name(String blog_name) {
        this.blog_name = blog_name;
    }

    public String getBlog_description() {
        return blog_description;
    }

    public void setBlog_description(String blog_description) {
        this.blog_description = blog_description;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
