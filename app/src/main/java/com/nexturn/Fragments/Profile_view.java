package com.nexturn.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nexturn.Activites.Profile;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.R;

import static com.nexturn.Activites.Profile.user_obj;

/**
 * Created by Prateek on 22-03-2017.
 */

public class Profile_view extends Fragment {
    ourTextView name, email, gender, mobile, dob, aadhar, loc;
    ImageView user_image;
    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile_frag, null);
        initialize_views();
        insert_data();
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

    void insert_data() {
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
}
