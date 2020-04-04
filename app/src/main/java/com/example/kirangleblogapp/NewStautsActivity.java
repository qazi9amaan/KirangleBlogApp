package com.example.kirangleblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kirangleblogapp.Adapters.BlogRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewStautsActivity extends AppCompatActivity {
    Toolbar mainToolbar;
    private CircleImageView userimage;
    private EditText input_status;
    private ImageView upload_btn;
    private ProgressBar progressbar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stauts);


        // Toolbar
        mainToolbar = (Toolbar)findViewById(R.id.new_status_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Uploading a status?");


        userimage = findViewById(R.id.user_image);
        input_status= findViewById(R.id.user_post_status);
        upload_btn=findViewById(R.id.upload_user_status);
        progressbar=findViewById(R.id.progressBar);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();


        //LOADING USER IMAGE
        firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userImage = task.getResult().getString("image");

                    RequestOptions placeholerOption = new RequestOptions();
                    placeholerOption.placeholder(R.drawable.action_add_post);
                    Glide.with(getApplicationContext()).applyDefaultRequestOptions(placeholerOption).load(userImage).into(userimage);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to fetch userdata from db", Toast.LENGTH_SHORT).show();
                }
            }
        });


        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_btn.setVisibility(View.INVISIBLE);
                progressbar.setVisibility(View.VISIBLE);
                UploadPost();
            }
        });


    }

    private void UploadPost() {
        String status = input_status.getText().toString();
        if(!TextUtils.isEmpty(status))
        {
            UploadToDatabase(status);
        }else{
            Toast.makeText(this, "Please provide some text", Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadToDatabase(String status) {

        Map<String,Object> postMap= new HashMap<>();

        postMap.put("status",status);
        postMap.put("user_id",currentUserId);
        postMap.put("timestamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Status").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(NewStautsActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                    SendToMain();
                }else {

                }
                progressbar.setVisibility(View.INVISIBLE);
                upload_btn.setVisibility(View.VISIBLE);
            }
        });



    }
    private void SendToMain() {
        Intent main_intent = new Intent(NewStautsActivity.this, MainActivity.class );
        startActivity(main_intent);
        finish();
    }
}
