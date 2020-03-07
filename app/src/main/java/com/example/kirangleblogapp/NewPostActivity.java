package com.example.kirangleblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 200;
    private  Toolbar newPostToolbar;
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostbtn;
    private ProgressBar newpostProgressbar;
    private Uri postImageUri;

    String download_thumbnail_uri;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        newPostToolbar= findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newpostProgressbar = findViewById(R.id.new_post_progressbar);
        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostbtn= findViewById(R.id.postbtn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();




        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);
            }
        });

        newPostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(desc)&& postImageUri != null)
                {
                    newpostProgressbar.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    final StorageReference filepath = storageReference.child("post_images").child(randomName+".jpg");
                    filepath.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        final String download_uri = uri.toString();
                                        File actualImageFile = new File(postImageUri.getPath());
                                        try {
                                            compressedImageFile = new Compressor(NewPostActivity.this)
                                                    .setMaxWidth(100)
                                                    .setMaxHeight(100)
                                                    .setQuality(2)
                                                    .compressToBitmap(actualImageFile);


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] thumbdata = baos.toByteArray();

                                        final UploadTask uploadTask = storageReference.child("post_images/thumbs").
                                                child(randomName+".jpg").putBytes(thumbdata);

                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                //For thumbnail
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();

                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                         download_thumbnail_uri =uri.toString();
                                                    }
                                                });

                                                // For uplaoding main image to database
                                                Map<String,Object> postMap= new HashMap<>();
                                                postMap.put("Image_url",download_uri);
                                                postMap.put("desc",desc);
                                                postMap.put("user_id",currentUserId);
                                                postMap.put("thumbnail",download_thumbnail_uri);
                                                postMap.put("timestamp",FieldValue.serverTimestamp());


                                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(NewPostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                                                            SendToMain();
                                                        }else {

                                                        }
                                                        newpostProgressbar.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                    //Error Hanlding
                                                Toast.makeText(NewPostActivity.this, "Thumbnail Error"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                newpostProgressbar.setVisibility(View.INVISIBLE);
                                            }

                                        });



                                    }
                                });
                        }

                    });

                }
            }
        });
    }

    private void SendToMain() {
        Intent main_intent = new Intent(NewPostActivity.this, MainActivity.class );
        startActivity(main_intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }


    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
