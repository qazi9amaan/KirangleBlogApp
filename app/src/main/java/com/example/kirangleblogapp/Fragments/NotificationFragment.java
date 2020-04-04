package com.example.kirangleblogapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kirangleblogapp.Adapters.BlogRecyclerAdapter;
import com.example.kirangleblogapp.Adapters.NotificationRecyclerAdapter;
import com.example.kirangleblogapp.LoginActivity;
import com.example.kirangleblogapp.Modals.BlogPost;
import com.example.kirangleblogapp.Modals.Notifications;
import com.example.kirangleblogapp.NewStautsActivity;
import com.example.kirangleblogapp.R;
import com.example.kirangleblogapp.RegisterActivity;
import com.example.kirangleblogapp.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    // Android Palette
    private RecyclerView notification_list_view;
    private ArrayList<Notifications> notification_list;


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private NotificationRecyclerAdapter notificationRecyclerAdapter;
    private String currentUserId;
    private TextView updatestatus;

    private CircleImageView profileimage;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Initializations
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notification_list_view = view.findViewById(R.id.notification_list_view);
        updatestatus = view.findViewById(R.id.textView3);
        profileimage = view.findViewById(R.id.circleImageView);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        notification_list = new ArrayList<>();
        notificationRecyclerAdapter = new NotificationRecyclerAdapter(getContext(),notification_list);

        notification_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(notification_list_view);
        notification_list_view.setAdapter(notificationRecyclerAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        updatestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToUpdateStatus();
            }
        });

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String username = task.getResult().getString("name");
                        String userImage = task.getResult().getString("image");

                        setUserData( userImage);
                    } else {
                        Toast.makeText(getContext(), "Unable to fetch userdata from db", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            firebaseFirestore.collection("Status")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                            // if() is used to checked whether the data is available
                            if (!documentSnapshots.isEmpty()) {



                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        String blogPostid = doc.getDocument().getId();
                                        Notifications blogPost = doc.getDocument().toObject(Notifications.class).withID(blogPostid);
                                        notification_list.add(blogPost);
                                        notificationRecyclerAdapter.notifyDataSetChanged();

                                    }
                                    notificationRecyclerAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Sorry no more posts!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        return view;
    }


    private void sendToUpdateStatus()
    {
        Intent reg_intent = new Intent(getContext(), NewStautsActivity.class);
        startActivity(reg_intent);
    }

    public void setUserData(String image) {



        RequestOptions placeholerOption = new RequestOptions();
        placeholerOption.placeholder(R.drawable.action_add_post);
        Glide.with(this).applyDefaultRequestOptions(placeholerOption).load(image).into(profileimage);

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT| ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            String user = notification_list.get(viewHolder.getAdapterPosition()).getUser_id();
            String id = notification_list.get(viewHolder.getAdapterPosition()).BlogPostId;
            if(currentUserId.equals(user))
            {
                notification_list.remove(viewHolder.getAdapterPosition());

                firebaseFirestore.collection("Posts").document(id).delete();
                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();

                notificationRecyclerAdapter.notifyDataSetChanged();

            }else {
                Toast.makeText(getContext(), "Successfully hidden!", Toast.LENGTH_SHORT).show();
                notificationRecyclerAdapter.notifyDataSetChanged();


            }


        }
    };



}
