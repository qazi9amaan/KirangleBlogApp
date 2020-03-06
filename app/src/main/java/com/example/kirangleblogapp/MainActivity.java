package com.example.kirangleblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String CurrentUserId;

    private FloatingActionButton addPostBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Kirangle Blog");


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        addPostBtn=findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginintent = new Intent(MainActivity.this , NewPostActivity.class);
                startActivity(loginintent);

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
           sendTOLogin();

        }else {
            CurrentUserId = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(CurrentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(!task.getResult().exists())
                    {
                        gotoSettings();
                    }
                }else{
                    String e = task.getException().getMessage();
                    Toast.makeText(MainActivity.this, e, Toast.LENGTH_SHORT).show();

                }

                }
            });
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_logout_btn :
                logout();
                return true;

            case R.id.action_settings_btn :
                gotoSettings();
                return true;


            default:
                return false;

        }


    }


    private void gotoSettings() {
        Intent loginintent = new Intent(MainActivity.this , SetupActivity.class);
        startActivity(loginintent);
        finish();
    }

    private void logout() {
        mAuth.signOut();
        sendTOLogin();
    }

    private void sendTOLogin() {
        Intent loginintent = new Intent(MainActivity.this , LoginActivity.class);
        startActivity(loginintent);
        finish();
    }

}
