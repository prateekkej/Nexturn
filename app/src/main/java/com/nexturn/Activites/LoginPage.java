package com.nexturn.Activites;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nexturn.R;
import com.nexturn.ModifiedViews.ourTextView;

public class LoginPage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
Button login;
    GoogleSignInOptions gso;
    GoogleApiClient googleApiClient;
    SignInButton googleButton;
    CallbackManager callbackManager;
    LoginButton fbButton;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login_page);
        login=(Button)findViewById(R.id.login_button);
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

        ourTextView signup = (ourTextView) findViewById(R.id.signupmail);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent im = new Intent(LoginPage.this, Registration.class);
                startActivity(im);
            }
        });
        login.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Raleway-Medium.ttf"));
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleButton=(SignInButton)findViewById(R.id.google);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent,200);
            }
        });

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       callbackManager.onActivityResult(requestCode, resultCode, data);

       if (requestCode == 200) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
if(result.isSuccess())
{GoogleSignInAccount acct = result.getSignInAccount();

    Toast.makeText(this,acct.getDisplayName(),Toast.LENGTH_LONG).show();
    Toast.makeText(this,acct.getEmail(),Toast.LENGTH_LONG).show();
    Toast.makeText(this,acct.getFamilyName(),Toast.LENGTH_LONG).show();
} else {
    Toast.makeText(this, "Failed Login", Toast.LENGTH_LONG).show();
}
       }
   }


}
