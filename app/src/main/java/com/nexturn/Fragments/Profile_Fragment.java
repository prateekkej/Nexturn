package com.nexturn.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.nexturn.Activites.HomeActivity;
import com.nexturn.Activites.LoginPage;
import com.nexturn.DatabaseUtil;
import com.nexturn.ModifiedViews.DatePickerFrag;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_object;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import static com.nexturn.Activites.HomeActivity.location_db;
import static com.nexturn.Activites.HomeActivity.user_obj;

/**
 * Created by Prateek on 22-03-2017.
 */

public class Profile_Fragment extends Fragment {
    public static ImageView user_image;
    public static ourTextView dob_edit;
    public static String nDOB;
    ourTextView name, email, gender, mobile, dob, aadhar, loc;
    boolean isEditingOn = false;
    View v;
    private Button reset, delete, edit;
    private EditText phone_edit, fname_edit, lname_edit;
    private Spinner gender_edit, location_edit;
    private String nName, nGender, nPhone, nLocation;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private PopupMenu pop;

    public static void changeImage(final Context ct) {
        if (HomeActivity.user_obj.imgURL != null) {

            Glide.with(ct).load(HomeActivity.user_obj.imgURL).asBitmap().centerCrop().into(new BitmapImageViewTarget(user_image) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(ct.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    user_image.setImageDrawable(circularBitmapDrawable);
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        edit.setText("Edit Profile");
        isEditingOn = false;
        updateProfile_view();
        view_details();
            }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialize_views(inflater);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = DatabaseUtil.getDatabase().getReference("users_info");
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop = new PopupMenu(getContext(), view);
                pop.getMenuInflater().inflate(R.menu.image_upload, pop.getMenu());
                pop.show();
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        CropImage.startPickImageActivity(getActivity());
                        return true;
                    }
                });
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditingOn) {
                    isEditingOn = false;
                    if (checkForValidData()) ;
                    submitData();
                    view_details();
                    edit.setText("Edit Profile");
                } else {
                    isEditingOn = true;
                    edit.setText("Confirm");
                    view_edits();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Resetting your password");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    new AlertDialog.Builder(getContext()).setMessage("Password reset successful.Please check your mail to proceed.\n" +
                                            "You are being signed out from the app as of now. ").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            HomeActivity.location_db.child(user_obj.uid).removeValue();
                                            firebaseAuth.signOut();
                                            LoginManager.getInstance().logOut();
                                            startActivity(new Intent(getContext(), LoginPage.class));
                                        }
                                    }).show();
                                } else {
                                    progressDialog.dismiss();
                                    new AlertDialog.Builder(getContext()).setMessage("Facing network issues .Try again.").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();

                                }
                            }
                        });
            }
        });
       /* delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog= new ProgressDialog(getContext());
                progressDialog.setTitle("Deleting your account");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                location_db.child(currentUser.getUid()).removeValue();
                HomeActivity.storageReference.child("user-images/" + firebaseAuth.getCurrentUser().getUid()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(),"Bye! Bye! Will miss your presence. :(",Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Error at user delete","");

                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });}})
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(StorageException.fromException(e).getErrorCode()==StorageException.ERROR_OBJECT_NOT_FOUND){
                            databaseReference.child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebaseAuth.getCurrentUser().delete();
                                }
                            });
                        }
                    }
                });


        }});*/
        return v;
    }

    void updateProfile_view() {
        if (HomeActivity.user_obj != null) {
            name.setText(HomeActivity.user_obj.fname + " " + user_obj.lname);
            email.setText(HomeActivity.user_obj.email);
            mobile.setText(HomeActivity.user_obj.mobile);
            aadhar.setText(HomeActivity.user_obj.aadhar);
            gender.setText(HomeActivity.user_obj.gender);
            dob.setText(HomeActivity.user_obj.dob);
            loc.setText(HomeActivity.user_obj.location);
            changeImage(getContext());
        }


    }

    public void view_details() {

        name.setVisibility(View.VISIBLE);
        fname_edit.setVisibility(View.GONE);
        lname_edit.setVisibility(View.GONE);
        gender.setVisibility(View.VISIBLE);
        gender_edit.setVisibility(View.GONE);
        dob.setVisibility(View.VISIBLE);
        dob_edit.setVisibility(View.GONE);
        loc.setVisibility(View.VISIBLE);
        location_edit.setVisibility(View.GONE);
        mobile.setVisibility(View.VISIBLE);
        phone_edit.setVisibility(View.GONE);
        reset.setVisibility(View.VISIBLE);

    }

    void view_edits() {
        int x = 0;
        String a[] = getResources().getStringArray(R.array.indian_states);
        for (int i = 0; i <= 35; i++) {
            if (a[i].equals(user_obj.location)) {
                x = i;
            }
        }
        name.setVisibility(View.GONE);
        fname_edit.setVisibility(View.VISIBLE);
        fname_edit.setText(user_obj.fname);
        lname_edit.setVisibility(View.VISIBLE);
        lname_edit.setText(user_obj.lname);
        gender.setVisibility(View.GONE);
        gender_edit.setVisibility(View.VISIBLE);
        dob.setVisibility(View.GONE);
        dob_edit.setVisibility(View.VISIBLE);
        dob_edit.setText(user_obj.dob);
        dob_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFrag dpf = new DatePickerFrag();
                dpf.setCaller(false);
                dpf.show(getFragmentManager(), "DatePicker");
            }
        });
        loc.setVisibility(View.GONE);
        location_edit.setVisibility(View.VISIBLE);
        location_edit.setSelection(x);
        mobile.setVisibility(View.GONE);
        phone_edit.setVisibility(View.VISIBLE);
        if (user_obj.mobile.equals("")) {
        } else {
            phone_edit.setText(user_obj.mobile);
        }
        reset.setVisibility(View.GONE);
    }

    public boolean checkForValidData() {

        nName = fname_edit.getText().toString().trim() + " " + lname_edit.getText().toString().trim();
        nGender = gender_edit.getSelectedItem().toString();
        nDOB = dob_edit.getText().toString();
        nLocation = location_edit.getSelectedItem().toString();
        nPhone = phone_edit.getText().toString();
        return true;
    }

    void submitData() {
        Map<String, Object> user_update = new HashMap<String, Object>();
        user_update.put("fname", fname_edit.getText().toString().trim());
        user_update.put("lname", lname_edit.getText().toString().trim());
        user_update.put("dob", nDOB);
        user_update.put("gender", nGender);
        user_update.put("location", nLocation);
        user_update.put("mobile", nPhone);
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(user_update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_LONG).show();
                name.setText(nName);
                dob.setText(nDOB);
                gender.setText(nGender);
                loc.setText(nLocation);
                mobile.setText(nPhone);
            }
        });

    }
    void initialize_views(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.profile_frag, null);
        name = (ourTextView) v.findViewById(R.id.name_profile);
        fname_edit = (EditText) v.findViewById(R.id.fname_edit);
        lname_edit = (EditText) v.findViewById(R.id.lname_edit);
        email = (ourTextView) v.findViewById(R.id.email_profile);
        gender = (ourTextView) v.findViewById(R.id.gender_profile);
        mobile = (ourTextView) v.findViewById(R.id.phone_profile);
        dob = (ourTextView) v.findViewById(R.id.dob_profile);
        user_image = (ImageView) v.findViewById(R.id.user_image_profile);
        edit = (Button) v.findViewById(R.id.edit);
        aadhar = (ourTextView) v.findViewById(R.id.aadhar_profile);
        loc = (ourTextView) v.findViewById(R.id.location_profile);
        reset = (Button) v.findViewById(R.id.reset_password);
        delete = (Button) v.findViewById(R.id.delete_account);
        phone_edit = (EditText) v.findViewById(R.id.phone_edit);
        gender_edit = (Spinner) v.findViewById(R.id.gender_edit);
        dob_edit = (ourTextView) v.findViewById(R.id.dob_edit);
        location_edit = (Spinner) v.findViewById(R.id.location_edit);
    }
}
