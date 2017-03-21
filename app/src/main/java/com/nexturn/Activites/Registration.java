package com.nexturn.Activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.PopupMenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nexturn.ModifiedViews.DatePickerFrag;
import com.nexturn.R;
import com.nexturn.User_object;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Permission;

public class Registration extends AppCompatActivity {
    final int STORAGE_PERM = 1;
    final int LOCATION_PERM = 2;
    boolean pass_visible = false;
    boolean valid_data;
    byte[] imgdecomp;
    private ProgressDialog pd;
    private String fnamestr, imgURL, lnamestr, emailstr, genderstr, dobstr, mobilestr, passstr, aadharstr, locationstr, uidstr;
    private Spinner state_list;
    private EditText fname, lname, aadhar, contact, email, location, password;
    private ImageView userimage;
    private PopupMenu pop;
    private RadioButton gen;
    private RadioGroup gender;
    private TextView dob;
    private User_object user_object;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth.signOut();
        databaseReference = FirebaseDatabase.getInstance().getReference("users_info");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERM);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location me = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        initialize_view();


    }
    public void initialize_view() {
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        dob = (TextView) findViewById(R.id.dobenter);
        aadhar = (EditText) findViewById(R.id.aadhar);
        email = (EditText) findViewById(R.id.email_id);
        password = (EditText) findViewById(R.id.pass);
        contact = (EditText) findViewById(R.id.phone);
        state_list = (Spinner) findViewById(R.id.state_list);
        state_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationstr = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        gender = (RadioGroup) findViewById(R.id.gendertick);
    }
    public void setState_list(View v) {
        DatePickerFrag df = new DatePickerFrag();
        df.show(getSupportFragmentManager(), "datepicker");
    }
    public void passs(View v) {
        if (pass_visible == false) {
            password.setTransformationMethod(null);
            pass_visible = true;
        } else {
            password.setTransformationMethod(new PasswordTransformationMethod());
            pass_visible = false;
        }
    }
    public void setUserImage(View v) {
        pop = new PopupMenu(this, v);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERM);
        } else {
            pop.getMenuInflater().inflate(R.menu.image_upload, pop.getMenu());
            pop.show();
            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent im = new Intent();
                    im.setType("image/*");
                    im.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(im, 1);

                    return true;
                }
            });

        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pop.getMenuInflater().inflate(R.menu.image_upload, pop.getMenu());
                    pop.show();
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent im = new Intent();
                            im.setType("image/*");
                            im.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(im, 1);
                            return true;
                        }
                    });
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(this, "Permission was denied for STORAGE .\nCannot access media .", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case LOCATION_PERM:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location me = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Toast.makeText(this, me.toString(), Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                else {

                    Toast.makeText(this, "Permission was denied for Location .\nCannot access Location .", Toast.LENGTH_LONG).show();
                }

                // other 'case' lines to check for other
                // permissions this app might request
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22 && resultCode == RESULT_OK) {
            Bitmap img = (Bitmap) data.getExtras().get("data");
            userimage = (ImageView) findViewById(R.id.userImage);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imgdecomp = byteArrayOutputStream.toByteArray();
            Glide.with(getApplicationContext()).load(byteArrayOutputStream.toByteArray()).asBitmap().centerCrop().into(new BitmapImageViewTarget(userimage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    userimage.setImageDrawable(circularBitmapDrawable);
                    userimage.setMaxWidth(100);
                    userimage.setMaxHeight(100);
                }
            });

        }
        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            Uri imgUri = data.getData();
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imgUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, 22);

        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_up:
                createUserObject();
                break;
        }
        return true;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_up_button, menu);
        return true;
    }
    public void createUserObject() {
        fnamestr = fname.getText().toString().trim();
        lnamestr = lname.getText().toString().trim();
        dobstr = dob.getText().toString().trim();
        gen = (RadioButton) findViewById(gender.getCheckedRadioButtonId());
        genderstr = gen.getText().toString().trim();
        aadharstr = aadhar.getText().toString().trim();
        passstr = password.getText().toString().trim();
        emailstr = email.getText().toString().trim();
        mobilestr = contact.getText().toString().trim();
        valid_data = check_for_empty();
        if (valid_data) {
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setTitle("Registering User....");
            pd.setMessage("Please wait \nWhile we get you registered...");
            pd.show();
            firebaseAuth.createUserWithEmailAndPassword(emailstr, passstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful() && task.isComplete()) {
                        uidstr = firebaseAuth.getCurrentUser().getUid();
                        if (imgdecomp != null) {
                            UploadTask uploadTask = storageReference.child("user-images/" + firebaseAuth.getCurrentUser().getUid()).putBytes(imgdecomp);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Image Upload Failed.\nYou can upload it later!!", Toast.LENGTH_LONG).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imgURL = taskSnapshot.getDownloadUrl().toString();
                                    user_object = new User_object(uidstr, fnamestr, lnamestr, emailstr, genderstr, dobstr, mobilestr, aadharstr, locationstr, imgURL);
                                    databaseReference.child(uidstr).setValue(user_object);
                                }
                            });

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Empty ,Enter Something ", Toast.LENGTH_LONG).show();
        }
    }
    public boolean check_for_empty() {
        if (fnamestr.isEmpty() || lnamestr.isEmpty() || emailstr.isEmpty() || aadharstr.isEmpty() || passstr.isEmpty() || mobilestr.isEmpty() || dobstr == "DD/MM/YYYY" || locationstr == "Select your State") {
            return false;
        } else {
            return true;
        }
    }
}
