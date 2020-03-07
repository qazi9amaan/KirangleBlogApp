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

public class RegisterActivity  extends AppCompatActivity {
    private EditText regEmailText;
    private EditText regPassText;
    private EditText regConfirmPassText;
    private Button regBtn;
    private Button regLoginBtn;

    private FirebaseAuth mAuth;
    private ProgressBar regProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        regEmailText = (EditText)findViewById(R.id.reg_email);
        regPassText = (EditText)findViewById(R.id.reg_password);
        regConfirmPassText = (EditText)findViewById(R.id.reg_confirm_password);
        regBtn = (Button)findViewById(R.id.regbtn);
        regLoginBtn = (Button)findViewById(R.id.reg_login_btn);
        regProcess = (ProgressBar)findViewById(R.id.regProgressbar);

        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToMain();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regEmail = regEmailText.getText().toString();
                String regPassword = regPassText.getText().toString();
                String confrimPassword = regConfirmPassText.getText().toString();
                if(!TextUtils.isEmpty(regEmail) && !TextUtils.isEmpty(regPassword) && !TextUtils.isEmpty(confrimPassword)) {
                    if(regPassword.equals(confrimPassword))
                    {
                        regProcess.setVisibility(View.VISIBLE);
                        createAccount(regEmail,regPassword);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Password doesnot match!", Toast.LENGTH_SHORT).show();
                    }
                }
                }

            private void createAccount(String email, String password) {
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            SendToSetup();
                        }else
                        {
                            String e = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, e, Toast.LENGTH_SHORT).show();
                        }
                        regProcess.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser cu = mAuth.getCurrentUser();
        if(cu != null)
        {
            SendToMain();
        }
    }
    private void SendToMain() {
        Intent main_intent = new Intent(RegisterActivity.this, MainActivity.class );
        startActivity(main_intent);
        finish();
    }

    private void SendToSetup() {
        Intent setup_intent = new Intent(RegisterActivity.this, SetupActivity.class );
        startActivity(setup_intent);
        finish();
    }


}
