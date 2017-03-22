package com.nexturn.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_object;
import com.theartofdev.edmodo.cropper.CropImage;

import static com.nexturn.Activites.HomeActivity.user_obj;

/**
 * Created by Prateek on 22-03-2017.
 */

public class Profile_Fragment extends Fragment {
    ourTextView name, email, gender, mobile, dob, aadhar, loc;
    ImageView user_image;
    ViewGroup vg;
    View v;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private PopupMenu pop;

    @Override
    public void onResume() {
        super.onResume();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_obj = dataSnapshot.child(currentUser.getUid()).getValue(User_object.class);
                name.setText(user_obj.fname + " " + user_obj.lname);
                email.setText(user_obj.email);
                mobile.setText(user_obj.mobile);
                aadhar.setText(user_obj.aadhar);
                gender.setText(user_obj.gender);
                dob.setText(user_obj.dob);
                loc.setText(user_obj.location);
                if (user_obj.imgURL != null) {
                    Glide.with(getContext()).load(user_obj.imgURL).asBitmap().centerCrop().into(new BitmapImageViewTarget(user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile_frag, null);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        vg = container;
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users_info");
        initialize_views();
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
        return v;
    }

    void initialize_views() {
        name = (ourTextView) v.findViewById(R.id.name_profile);
        email = (ourTextView) v.findViewById(R.id.email_profile);
        gender = (ourTextView) v.findViewById(R.id.gender_profile);
        mobile = (ourTextView) v.findViewById(R.id.phone_profile);
        dob = (ourTextView) v.findViewById(R.id.dob_profile);
        user_image = (ImageView) v.findViewById(R.id.user_image_profile);
        aadhar = (ourTextView) v.findViewById(R.id.aadhar_profile);
        loc = (ourTextView) v.findViewById(R.id.location_profile);
    }


}
