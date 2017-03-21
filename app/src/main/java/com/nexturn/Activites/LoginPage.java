package com.nexturn.Activites;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nexturn.R;

public class LoginPage extends AppCompatActivity {
    private Button login;
    private CallbackManager callbackManager;
    private LoginButton fbButton;
    private String emailstr, passstr;
    private EditText email, pass;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        fb();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginPage.this, Profile.class));
        }
        email = (EditText) findViewById(R.id.login_email);
        pass = (EditText) findViewById(R.id.login_password);
        login=(Button)findViewById(R.id.login_button);
        login.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf"));


    }
    public void forgotpass(View v) {
        emailstr = email.getText().toString();
        if (emailstr.isEmpty()) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(emailstr).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful() && task.isComplete()) {
                        Toast.makeText(getApplicationContext(), "Password succesfully reset.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Reset Fail.Try Again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public void logmein(View v) {
        emailstr = email.getText().toString();
        passstr = pass.getText().toString();
        if (emailstr.isEmpty()) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(emailstr, passstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete() && task.isSuccessful()) {
                        startActivity(new Intent(LoginPage.this, Profile.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please check Email/Password", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void signup(View v) {
        Intent im = new Intent(LoginPage.this, Registration.class);
        startActivity(im);
    }
    void fb() {
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        fbButton = (LoginButton) findViewById(R.id.fbButton);
        fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), loginResult.getAccessToken().getUserId(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException e) {
            }
        });


    }
}
