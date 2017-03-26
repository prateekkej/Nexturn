package com.nexturn.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.nexturn.Activites.HomeActivity;
import com.nexturn.DatabaseUtil;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;
import com.nexturn.User_object;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.zip.Inflater;

import static com.nexturn.Activites.HomeActivity.user_obj;

/**
 * Created by Prateek on 22-03-2017.
 */

public class Profile_Fragment extends Fragment {
    public static ImageView user_image;
    ourTextView name, email, gender, mobile, dob, aadhar, loc;
    ViewGroup vg;
    View v;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialize_views(inflater);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        vg = container;
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
        return v;
    }

    void initialize_views(LayoutInflater inflater) {
        v = inflater.inflate(R.layout.profile_frag, null);
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
