package com.example.kirangleblogapp.Adapters;

import android.content.Context;
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
import com.example.kirangleblogapp.Modals.BlogPost;
import com.example.kirangleblogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;
    public BlogRecyclerAdapter(List<BlogPost> blog_list){
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        context = parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // id for each blog
        final String blogpostid = blog_list.get(position).BlogPostId;
        final String currentUserID = mAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String user_thumbNailUri = blog_list.get(position).getImage_thumb();
        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url,user_thumbNailUri);

        // USER DATA
        String user_id = blog_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        String username = task.getResult().getString("name");
                        String userImage = task.getResult().getString("image");
                        
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


        //LIKES
        holder.blogLikeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> likesMap = new HashMap<>();
                likesMap.put("timestamp", FieldValue.serverTimestamp());
                firebaseFirestore.collection("Posts/"+blogpostid+"/Likes").document(currentUserID).set(likesMap);
            }
        });

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            // initiaze likes
            blogLikeImageBtn=mView.findViewById(R.id.blog_like_btn);

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
            placeholerOption.placeholder(R.drawable.avatar);

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
            placeholerOption.placeholder(R.drawable.avatar);
            Glide.with(context).applyDefaultRequestOptions(placeholerOption).load(image).into(blogUserImage);

        }

    }
}
