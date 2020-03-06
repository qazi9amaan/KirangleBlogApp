package com.example.kirangleblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;

    private FloatingActionButton addPostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Kirangle Blog");


        mAuth = FirebaseAuth.getInstance();
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
