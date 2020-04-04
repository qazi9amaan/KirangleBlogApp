package com.example.kirangleblogapp.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kirangleblogapp.Modals.Likes;
import com.example.kirangleblogapp.Modals.Notifications;
import com.example.kirangleblogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Notifications> likes_list;
    private FirebaseAuth mAuth;
    private String currentUserId;


    private FirebaseFirestore firebaseFirestore;





    public NotificationRecyclerAdapter(Context c, ArrayList<Notifications> n  )
    {
        this.context=c;
        this.likes_list=n;
    }


    @NonNull
    @Override
    public NotificationRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        firebaseFirestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        return new NotificationRecyclerAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.feed_item_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);





        // USER DATA
        final String user_id = likes_list.get(position).getUser_id();

        String desc_data = likes_list.get(position).getStatus();
        //TIME
        long milliseconds = likes_list.get(position).getTimestamp().getTime();

        // Converting milliseconds into dateString
        final String dateString = DateFormat.format("MM/dd/yyyy",new Date(milliseconds)).toString();

        holder.setstatus(desc_data);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String userImage = task.getResult().getString("image");
                    String user = task.getResult().getString("name");


                    holder.setUserData(userImage);
                    holder.setUserInfo(user,dateString);
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

        TextView blogUserStatus , UserInfo;
        CircleImageView blogUserImage ;
        private View mView;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;





        }


        public void setUserInfo( String user,String date)
        {
            UserInfo = mView.findViewById(R.id.textView5);
            String status = user+" at "+date;
            UserInfo.setText(status);

        }

        public void setUserData(String image)
        {
            blogUserImage = mView.findViewById(R.id.user_status_picture);

            RequestOptions placeholerOption = new RequestOptions();
            placeholerOption.placeholder(R.drawable.action_add_post);
            Glide.with(context).applyDefaultRequestOptions(placeholerOption).load(image).into(blogUserImage);

        }

        public void  setstatus(String status)
        {
            blogUserStatus = mView.findViewById(R.id.user_status);
            blogUserStatus.setText(status);


        }

    }


}
