package com.example.kirangleblogapp.Modals;

import java.util.Date;

public class Likes {
    public String post_owner_id, post_liker_id,postid;
    public Date timestamp;

    public Likes(String post_owner_id, String post_liker_id, String postid, Date timestamp) {
        this.post_owner_id = post_owner_id;
        this.post_liker_id = post_liker_id;
        this.postid = postid;
        this.timestamp = timestamp;
    }

    public Likes() {
    }

    public String getPost_owner_id() {
        return post_owner_id;
    }

    public void setPost_owner_id(String post_owner_id) {
        this.post_owner_id = post_owner_id;
    }

    public String getPost_liker_id() {
        return post_liker_id;
    }

    public void setPost_liker_id(String post_liker_id) {
        this.post_liker_id = post_liker_id;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
