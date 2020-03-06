package com.example.kirangleblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private EditText setupName;
    private Button setupBtn;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private ProgressBar setUpProgessBar;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setupName = findViewById(R.id.setupname);
        setupBtn = findViewById(R.id.setupbtn);
        setupImage = findViewById(R.id.profile_image);
        setUpProgessBar = findViewById(R.id.setupprogressBar);

        Toolbar setuptoolbar = findViewById(R.id.setuptoolbar);
        setSupportActionBar(setuptoolbar);
        getSupportActionBar().setTitle("Account Setup");


        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = setupName.getText().toString();
                if(!TextUtils.isEmpty(username) && mainImageURI !=null)
                {
                    setUpProgessBar.setVisibility(View.VISIBLE);
                    final String user_id = mAuth.getCurrentUser().getUid();
                    StorageReference image_path = storageReference.child("profile_images").child(user_id+".jpg");

                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(SetupActivity.this, "Uploading", Toast.LENGTH_SHORT).show();

                                String download_uri = task.getResult().getDownloadUrl().toString();

                                Map<String,String> userMap = new HashMap<>();
                                userMap.put("name",username);
                                userMap.put("image",download_uri);

                                firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(SetupActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            SendToMain();
                                        }
                                        else{
                                            String e = task.getException().getMessage();
                                            Toast.makeText(SetupActivity.this, "Storage error"+e, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                setUpProgessBar.setVisibility(View.INVISIBLE);
                            }
                            else{
                                String e = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "Image Error"+e, Toast.LENGTH_SHORT).show();

                            }
                            setUpProgessBar.setVisibility(View.INVISIBLE);

                        }
                    });
                }
            }
        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                          ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else {
                        BringImagePicker();
                    }
                }else
                {
                    BringImagePicker();
                }
            }
        });
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }


    private void SendToMain() {
        Intent main_intent = new Intent(SetupActivity.this, MainActivity.class );
        startActivity(main_intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}
