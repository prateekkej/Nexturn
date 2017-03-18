package com.nexturn;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginPage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
Button login;
    GoogleSignInOptions gso;
    GoogleApiClient googleApiClient;
    SignInButton googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        login=(Button)findViewById(R.id.login_button);
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
        if (requestCode == 200) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
if(result.isSuccess())
{GoogleSignInAccount acct = result.getSignInAccount();
    Toast.makeText(this,acct.getDisplayName(),Toast.LENGTH_LONG).show();
    Toast.makeText(this,acct.getEmail(),Toast.LENGTH_LONG).show();
    Toast.makeText(this,acct.getFamilyName(),Toast.LENGTH_LONG).show();
    }else{

    Toast.makeText(this,"Failed Login",Toast.LENGTH_LONG).show();

}
        }
    }
}
