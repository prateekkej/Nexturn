package com.nexturn.ModifiedViews;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nexturn.R;
import com.nexturn.User_location;

/**
 * Created by Prateek on 27-03-2017.
 */

public class User_details_card extends AppCompatDialogFragment {
    public View v;
    private ourTextView name, phone, goingTo;
    private ImageView fblink, call, message, user_image, close;
    private User_location user;
    private Intent im;
    private String namestr, emailstr, phonestr, goingTostr, fblinkstr;

    public User_details_card() {
        super();
    }

    public void setUserObject(User_location abc) {
        user = abc;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        v = inflater.inflate(R.layout.person_info_card, null, false);
        intialize_views();
        namestr = user.name;
        phonestr = user.phone;
        goingTostr = user.goingTo;
        fblinkstr = user.fblink;
        name.setText(namestr);
        phone.setText(phonestr);
        goingTo.setText(goingTostr);
        if (user.imgURL != null && !user.imgURL.equals("")) {
            Glide.with(getActivity()).load(user.imgURL).asBitmap().fitCenter().into(new BitmapImageViewTarget(user_image) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    user_image.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        fblink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fblinkstr == null) {
                    new AlertDialog.Builder(getActivity()).setMessage("The person has not yet linked his/her Facebook ID").setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                } else {
                    im = new Intent(Intent.ACTION_VIEW);
                    Uri fb = Uri.parse(fblinkstr);
                    im.setData(fb);
                    startActivity(im);
                }
            }
        });
        if (!phonestr.isEmpty()) {
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    im = new Intent(Intent.ACTION_DIAL);
                    Uri call = Uri.parse("tel:" + phonestr);
                    im.setData(call);
                    startActivity(im);
                }
            });
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse("smsto:" + phonestr);
                    im = new Intent(Intent.ACTION_SENDTO, uri);
                    im.putExtra("sms_body:", "Hey!!\n Write your lift proposal here..  \n\n\n" + "I am at: " + user.getLatLng().toString());
                    startActivity(im);
                }
            });
        } else {
            new AlertDialog.Builder(getActivity()).setMessage("The person has not yet registered/verified his/her Phone Number").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });


        return v;
    }

    void intialize_views() {
        name = (ourTextView) v.findViewById(R.id.card_name);
        phone = (ourTextView) v.findViewById(R.id.card_phone);
        goingTo = (ourTextView) v.findViewById(R.id.card_destination);
        fblink = (ImageView) v.findViewById(R.id.card_fb);
        call = (ImageView) v.findViewById(R.id.card_call);
        message = (ImageView) v.findViewById(R.id.card_sms);
        user_image = (ImageView) v.findViewById(R.id.card_image);
        close = (ImageView) v.findViewById(R.id.card_close);

    }
}
