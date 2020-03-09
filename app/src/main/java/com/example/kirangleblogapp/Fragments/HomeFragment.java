package com.example.kirangleblogapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kirangleblogapp.Adapters.BlogRecyclerAdapter;
import com.example.kirangleblogapp.Modals.BlogPost;
import com.example.kirangleblogapp.R;
import com.google.firebase.auth.FirebaseAuth;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    // Android Palette
    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;

    private Boolean isFirstPageFirstLoaded = true;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Initializations
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        blog_list_view = view.findViewById(R.id.blog_list_view);
        firebaseAuth = FirebaseAuth.getInstance();


        blog_list = new ArrayList<>();
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);

        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(blogRecyclerAdapter);



        if(firebaseAuth.getCurrentUser() != null)
        {

            firebaseFirestore = FirebaseFirestore.getInstance();

            // to start more post once we 3 posts are reached
            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom)
                    {
                     //   Toast.makeText(container.getContext(), "Reached", Toast.LENGTH_SHORT).show();
                        loadMorePost();
                    }
                }
            });


            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(3);

            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                    // fixing bug in paginations
                    if(isFirstPageFirstLoaded) {
                        // last visited for pagination
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);
                    }
                    // will check the change in the document only if doc added
                    for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                        // Document is added
                        if(doc.getType()== DocumentChange.Type.ADDED){

                            // used extendable class to pass blog id for likes
                            String blogPostid = doc.getDocument().getId();

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withID(blogPostid);
                            // for loading the new image at top
                            if(isFirstPageFirstLoaded) {
                                blog_list.add(blogPost);
                            }else {
                                blog_list.add(0,blogPost);
                            }
                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                    isFirstPageFirstLoaded = false;
                }
            });
        }

        // Inflate the layout for this fragment
        return view;
    }

    public void loadMorePost()
    {

        // for more pagination
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                
                // if() is used to checked whether the data is available
                if(!documentSnapshots.isEmpty()){
                
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);
                        // will check the change in the document only if doc added
                        for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
        
                            // Document is added
                            if(doc.getType()== DocumentChange.Type.ADDED){

                                String blogPostid = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withID(blogPostid);

                                blog_list.add(blogPost);
                                blogRecyclerAdapter.notifyDataSetChanged();
        
                            }
                        }
                }else {
                    Toast.makeText(getActivity(), "Sorry no more posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
