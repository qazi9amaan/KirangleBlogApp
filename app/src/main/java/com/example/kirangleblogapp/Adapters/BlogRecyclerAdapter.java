package com.example.kirangleblogapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kirangleblogapp.LoginActivity;
import com.example.kirangleblogapp.MainActivity;
import com.example.kirangleblogapp.Modals.BlogPost;
import com.example.kirangleblogapp.R;
import com.example.kirangleblogapp.ShowLikersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseAuth mAuth;
    private String currentUserId;


    private FirebaseFirestore firebaseFirestore;
    public BlogRecyclerAdapter(List<BlogPost> blog_list){
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        context = parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // for performance
        holder.setIsRecyclable(false);

        // id for each blog
        final String blogpostid = blog_list.get(position).BlogPostId;
        final String currentUserID = mAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String user_thumbNailUri = blog_list.get(position).getImage_thumb();
        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url,user_thumbNailUri);

        // USER DATA
        final String user_id = blog_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        String username = task.getResult().getString("name");
                        String userImage = task.getResult().getString("image");

                        if(user_id.equals(currentUserId))
                        {
                            holder.blogDeleteImageBtn.setVisibility(View.VISIBLE);
                        }

                        holder.setUserData(username,userImage);
                    }else {
                        Toast.makeText(context, "Unable to fetch userdata from db", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        //TIME
        long milliseconds = blog_list.get(position).getTimestamp().getTime();

        // Converting milliseconds into dateString
        String dateString = DateFormat.format("MM/dd/yyyy",new Date(milliseconds)).toString();
        holder.setTime(dateString);


        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentUserID)) {
            // GET LIKES IMAGES REALTIME
            firebaseFirestore.collection("Posts/" + blogpostid + "/Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {
                        holder.blogLikeImageBtn.setImageDrawable(context.getDrawable(R.drawable.action_liked));
                    } else {
                        holder.blogLikeImageBtn.setImageDrawable(context.getDrawable(R.drawable.action_unliked));
                    }
                }
            });

            // GET LIKES COUNT REALTIME CANNOT BE DONE IN PREVIOUS AS WE USE CURRENT USER ID THERE
            firebaseFirestore.collection("Posts/" + blogpostid + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        holder.updateLikesCount(count);
                    } else {
                        holder.updateLikesCount(0);
                    }
                }
            });


            //LIKES
            holder.blogLikeImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    firebaseFirestore.collection("Posts/" + blogpostid + "/Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {



                                firebaseFirestore.collection("Posts").document(blogpostid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String postowner = task.getResult().getString("user_id");
                                            Map<String, Object> likesMap = new HashMap<>();
                                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                                            likesMap.put("postid", blogpostid);
                                            likesMap.put("post_liker_id", currentUserID);
                                            likesMap.put("post_owner_id", postowner);
                                            firebaseFirestore.collection("Posts/" + blogpostid + "/Likes").document(currentUserID).set(likesMap);


                                        } else {
                                            Toast.makeText(context, "Unable to fetch notifcation user from db", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                // DELETEING THE LIKE
                                firebaseFirestore.collection("Posts/" + blogpostid + "/Likes").document(currentUserID).delete();

                            }
                        }
                    });

                }
            });

        //DELETE POST
        holder.blogDeleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firebaseFirestore.collection("Posts").document(blogpostid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error in deleting :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        holder.blogLikeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginintent = new Intent(context , ShowLikersActivity.class);
                loginintent.putExtra("blog_id",blogpostid);
                context.startActivity(loginintent);

            }
        });
}

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView descView , blogdate;
        private ImageView blogImageView;

        private TextView blogUserName;
        private CircleImageView blogUserImage;

        // LIKES
        private ImageView blogLikeImageBtn;
        private TextView blogLikeCount;
        private ImageView blogDeleteImageBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            // initiaze likes
            blogLikeImageBtn=mView.findViewById(R.id.blog_like_btn);
            blogDeleteImageBtn=mView.findViewById(R.id.blog_delete_btn);
            blogLikeCount=mView.findViewById(R.id.blog_like_count);


        }

        public void setDescText(String descText)
        {
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri,String thumbnaildownloadUri)
        {
            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions placeholerOption = new RequestOptions();
            placeholerOption.placeholder(R.drawable.action_add_post);

            Glide.with(context).applyDefaultRequestOptions(placeholerOption).load(downloadUri).into(blogImageView);
            //LoadingThumbnail
            //Glide.with(context).applyDefaultRequestOptions(placeholerOption).load(downloadUri).thumbnail(Glide.with(context).load(thumbnaildownloadUri)).into(blogImageView);
        }

        public void setTime(String date)
        {
            blogdate = mView.findViewById(R.id.blog_date);

            blogdate.setText(date);
        }

        public void setUserData(String name,String image)
        {
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);
            RequestOptions placeholerOption = new RequestOptions();
            placeholerOption.placeholder(R.drawable.action_add_post);
            Glide.with(context).applyDefaultRequestOptions(placeholerOption).load(image).into(blogUserImage);

        }


        //SETTING LIKES COUNT
        public void updateLikesCount(int count)
        {
            blogLikeCount.setText(count+" Likes");
        }

    }
}
