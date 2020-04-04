package com.example.kirangleblogapp.Modals;

import com.example.kirangleblogapp.ExtendableClasses.BlogPostId;

import java.util.Date;

public class Notifications extends BlogPostId {
    public String user_id, status;
    public Date timestamp;

    public Notifications(String user_id, String status, Date timestamp) {
        this.user_id = user_id;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Notifications() {
    }
}
