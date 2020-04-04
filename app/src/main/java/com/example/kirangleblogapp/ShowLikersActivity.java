package com.example.kirangleblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kirangleblogapp.Adapters.LikesRecyclerAdapter;
import com.example.kirangleblogapp.Modals.Likes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowLikersActivity extends AppCompatActivity {

    private String BlogId;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private  String currentUserId;

    private CircleImageView blog_post_img;
    private TextView blog_post_desc;

    private RecyclerView likes_list_view;
    private ArrayList<Likes> likes_list;
    private LikesRecyclerAdapter LikesRecyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_likers);

        blog_post_img = findViewById(R.id.blog_post_image);
        blog_post_desc= findViewById(R.id.blog_post_desc);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();




        likes_list_view = findViewById(R.id.likes_recycler_view);
        likes_list = new ArrayList<>();
        LikesRecyclerAdapter = new LikesRecyclerAdapter(this,likes_list);

        likes_list_view.setLayoutManager(new LinearLayoutManager(this));
        likes_list_view.setAdapter(LikesRecyclerAdapter);





        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            BlogId = bundle.getString("blog_id");
        }


        firebaseFirestore.collection("Posts").document(BlogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String img = task.getResult().getString("image_url");
                    String desc = task.getResult().getString("desc");
                    setBlogData(desc,img);
                }
            }
        });


        if(firebaseAuth.getCurrentUser() != null) {

                    firebaseFirestore.collection("Posts/" + BlogId + "/Likes").

            orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(this,
                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                            // if() is used to checked whether the data is available
                            if (!documentSnapshots.isEmpty()) {


                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    // Document is added
                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        Likes blogPost = doc.getDocument().toObject(Likes.class);
                                        String user = blogPost.getPost_liker_id();

                                            likes_list.add(blogPost);
                                            LikesRecyclerAdapter.notifyDataSetChanged();




                                    }
                                }
                            } else {
                                Toast.makeText(ShowLikersActivity.this, "Sorry no more posts!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }



    }


    public void setBlogData(String desc,String image)
    {

        blog_post_desc.setText(desc);
        RequestOptions placeholerOption = new RequestOptions();
        placeholerOption.placeholder(R.drawable.action_add_post);
        Glide.with(this).applyDefaultRequestOptions(placeholerOption).load(image).into(blog_post_img);

    }




}
