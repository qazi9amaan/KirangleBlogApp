package com.example.kirangleblogapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kirangleblogapp.Adapters.BlogRecyclerAdapter;
import com.example.kirangleblogapp.Modals.BlogPost;
import com.example.kirangleblogapp.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    // Android Palette
    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;

    private FirebaseFirestore firebaseFirestore;

    private BlogRecyclerAdapter blogRecyclerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initializations
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        blog_list_view = view.findViewById(R.id.blog_list_view);

        blog_list = new ArrayList<>();
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);

        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(blogRecyclerAdapter);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                       // will check the change in the document only if doc added
                        for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                            // Document is added
                                if(doc.getType()== DocumentChange.Type.ADDED){
                                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                                        blog_list.add(blogPost);
                                        blogRecyclerAdapter.notifyDataSetChanged();

                                }
                        }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
