package com.example.kirangleblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private FirebaseAuth mAuth;
    private ProgressBar loginProcess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginEmailText = (EditText)findViewById(R.id.login_email);
        loginPassText = (EditText)findViewById(R.id.login_password);
        loginBtn = (Button)findViewById(R.id.loginbtn);
        loginRegBtn = (Button)findViewById(R.id.login_reg_btn);
        loginProcess = (ProgressBar)findViewById(R.id.loginProgressbar);

        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToRegister();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess.setVisibility(View.VISIBLE);
                String loginEmail = loginEmailText.getText().toString();
                String loginPassword = loginPassText.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword) ){
                        loginProcess.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    SendToMain();
                                }
                                else{
                                    String e = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, e, Toast.LENGTH_SHORT).show();
                                }

                                loginProcess.setVisibility(View.INVISIBLE);
                            }
                        });
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
           SendToMain();

        }
    }

    private void SendToMain() {
        Intent main_intent = new Intent(LoginActivity.this, MainActivity.class );
        startActivity(main_intent);
        finish();
    }

    private void SendToRegister() {
        Intent reg_intent = new Intent(LoginActivity.this, RegisterActivity.class );
        startActivity(reg_intent);
        finish();
    }


}
