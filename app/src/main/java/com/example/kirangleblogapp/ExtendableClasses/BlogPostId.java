package com.example.kirangleblogapp.ExtendableClasses;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {

    @Exclude
    public String BlogPostId;

    public <T extends com.example.kirangleblogapp.ExtendableClasses.BlogPostId> T withID(@NonNull final String id)
    {
        this.BlogPostId = id;
        return (T) this;
    }

}
