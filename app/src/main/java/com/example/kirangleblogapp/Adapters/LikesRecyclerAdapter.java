package com.example.kirangleblogapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kirangleblogapp.Modals.Likes;
import com.example.kirangleblogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesRecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<Likes> likes_list;
    private FirebaseAuth mAuth;
    private String currentUserId;


    private FirebaseFirestore firebaseFirestore;





    public LikesRecyclerAdapter(Context c, ArrayList<Likes> n  )
    {
        this.context=c;
        this.likes_list=n;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        firebaseFirestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.likers_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);





        // USER DATA
        final String user_id = likes_list.get(position).getPost_liker_id();
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


    }

    @Override
    public int getItemCount() {
        return likes_list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder   {

        TextView  blogUserName;
        CircleImageView blogUserImage ;
        private View mView;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


        }


        public void setUserData(String name,String image)
        {
            blogUserImage = mView.findViewById(R.id.likers_user_image);
            blogUserName = mView.findViewById(R.id.likers_user_name);

            blogUserName.setText(name);
            RequestOptions placeholerOption = new RequestOptions();
            placeholerOption.placeholder(R.drawable.action_add_post);
            Glide.with(context).applyDefaultRequestOptions(placeholerOption).load(image).into(blogUserImage);

        }



    }




}
