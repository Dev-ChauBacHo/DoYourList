package com.chaubacho.doyourlist2.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaubacho.doyourlist2.MainActivity;
import com.chaubacho.doyourlist2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button btnLogin, btnSignup;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.edit_text_email);
        password = findViewById(R.id.edit_text_password);
        btnLogin = findViewById(R.id.button_loggin);
        btnSignup = findViewById(R.id.button_logup);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void signup() {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Register successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Something is wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void signin() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Signin sucessfully
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Something is wrong", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

    }
}