package com.nexturn.Activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nexturn.DatabaseUtil;
import com.nexturn.R;
import com.nexturn.User_object;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginPage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Button login;
    private String dob, gender, mobile, location;
    private String emailstr, passstr;
    private EditText email, pass;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton googleButton;
    private DatabaseReference databaseReference;
    private CallbackManager mCallbackManager;
    private Profile fbProfile;
    private ProfileTracker fbProfileTracker;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }

        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Google Sign-in");
        pd.setMessage("Logging you in...");
        pd.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(User_object.class) == null) {
                                            databaseReference.child(currentUser.getUid()).setValue(new User_object(currentUser.getUid(),
                                                    currentUser.getDisplayName(), "", currentUser.getEmail()
                                                    , "", "", "", "", "", currentUser.getPhotoUrl().toString(), "G", null));
                                            pd.dismiss();
                                        } else {
                                            Map<String, Object> update_map = new HashMap<String, Object>();
                                            update_map.put("gg", 1);
                                            databaseReference.child(currentUser.getUid()).updateChildren(update_map);
                                            pd.dismiss();
                                            databaseReference.removeEventListener(this);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                pd.dismiss();
                                finish();
                                mGoogleApiClient.clearDefaultAccountAndReconnect();
                                startActivity(new Intent(LoginPage.this, HomeActivity.class));
                            }
                        } else {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Sign in with Google Failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginManager.getInstance().logOut();
        databaseReference = DatabaseUtil.getDatabase().getReference("users_info");
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        setContentView(R.layout.activity_login_page);
        signInWithFacebook();
        firebaseAuth = FirebaseAuth.getInstance();
        googleButton = (SignInButton) findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinwithGoogle(view);
            }
        });
        email = (EditText) findViewById(R.id.login_email);
        pass = (EditText) findViewById(R.id.login_password);
        login=(Button)findViewById(R.id.login_button);
        login.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf"));


    }

    public void signinwithGoogle(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1);
    }

    public void signInWithFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.fbButton);
        loginButton.setReadPermissions("email", "public_profile", "user_birthday", "user_location");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.d("FB:", "facebook:onSuccess:" + loginResult);
                if (Profile.getCurrentProfile() == null) {
                    fbProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            fbProfile = profile2;
                            fbProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                } else {
                    fbProfile = Profile.getCurrentProfile();
                }
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            gender = object.getString("gender");
                            dob = object.getString("birthday");
                            location = object.getJSONObject("location").getString("name");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "gender,birthday,location");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d("FB:", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FB:", "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Facebook Sign-in");
        pd.setMessage("Logging you in...");
        pd.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(User_object.class) == null) {
                                            databaseReference.child(currentUser.getUid()).setValue(new User_object(currentUser.getUid(),
                                                    fbProfile.getFirstName(), fbProfile.getLastName(), currentUser.getEmail()
                                                    , gender, dob, "", "", location, fbProfile.getProfilePictureUri(400, 400).toString(), "F",
                                                    fbProfile.getLinkUri().toString()));
                                            pd.dismiss();
                                        } else {
                                            Map<String, Object> update_user = new HashMap<String, Object>();
                                            update_user.put("gender", gender);
                                            update_user.put("location", location);
                                            update_user.put("dob", dob);
                                            update_user.put("fb", 1);
                                            update_user.put("fblink", fbProfile.getLinkUri().toString());
                                            update_user.put("imgURL", fbProfile.getProfilePictureUri(400, 400).toString());
                                            databaseReference.child(currentUser.getUid()).updateChildren(update_user);
                                            pd.dismiss();
                                            databaseReference.removeEventListener(this);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                pd.dismiss();

                                finish();
                                startActivity(new Intent(LoginPage.this, HomeActivity.class));
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign in with Facebook failed.", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void forgotpass(View v) {
        emailstr = email.getText().toString();
        if (emailstr.isEmpty()) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_LONG).show();
        } else {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Please wait...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
            firebaseAuth.sendPasswordResetEmail(emailstr).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful() && task.isComplete()) {
                        Toast.makeText(getApplicationContext(), "Password succesfully reset.", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Reset Fail.Try Again", Toast.LENGTH_LONG).show();
                        pd.dismiss();
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
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Logging you in ...");
            pd.setMessage("Please wait...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
            pd.setCancelable(false);
            firebaseAuth.signInWithEmailAndPassword(emailstr, passstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete() && task.isSuccessful()) {
                        pd.dismiss();
                        finish();
                        startActivity(new Intent(LoginPage.this, HomeActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Please check Email/Password", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void signup(View v) {
        Intent im = new Intent(LoginPage.this, Registration.class);
        startActivity(im);
    }
    }
