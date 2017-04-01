package com.nexturn.ModifiedViews;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nexturn.Activites.HomeActivity;
import com.nexturn.R;
import com.nexturn.User_location;

/**
 * Created by Prateek on 27-03-2017.
 */

public class User_details_card extends AppCompatDialogFragment {
    public View mainDialog, proposalDialog;
    private Button cancel_proposal, send_proposal;
    private EditText proposal;
    private ourTextView name, phone, goingTo;
    private ImageView fblink, call, message, user_image, close;
    private User_location user;
    private Intent im;
    private AlertDialog proposalInput;
    private String namestr, emailstr, phonestr, goingTostr, fblinkstr;

    public User_details_card() {
        super();
    }

    public void setUserObject(User_location abc) {
        user = abc;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainDialog = inflater.inflate(R.layout.person_info_card, null, false);
        proposalDialog = inflater.inflate(R.layout.send_proposal_layout, null, false);
        intialize_views();
        namestr = user.name;
        phonestr = user.phone;
        goingTostr = user.goingTo;
        fblinkstr = user.fblink;
        name.setText(namestr);
        phone.setText(phonestr);
        goingTo.setText(goingTostr);
        getDialog().setTitle("User Details");
        if (user.imgURL != null || !user.imgURL.equals("")) {
            try {
                Glide.with(getActivity()).load(user.imgURL).asBitmap().fitCenter().into(new BitmapImageViewTarget(user_image) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    user_image.setImageDrawable(circularBitmapDrawable);
                }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!phonestr.isEmpty()) {
                    im = new Intent(Intent.ACTION_DIAL);
                    Uri call = Uri.parse("tel:" + phonestr);
                    im.setData(call);
                    startActivity(im);
                    } else {
                        new AlertDialog.Builder(getActivity()).setMessage("The person has not yet registered/verified his/her Phone Number").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    }
                }
            });
        proposalInput = new AlertDialog.Builder(getContext()).setView(proposalDialog).create();
        message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  /*  if (!phonestr.isEmpty()) {
                        Uri uri = Uri.parse("smsto:" + phonestr);
                    im = new Intent(Intent.ACTION_SENDTO);
                    im.setData(uri);
                    im.putExtra("sms_body", "Hey!!\n Write your lift proposal here..  \n\n\n" + "I am at: " + user.getLatLng().toString());
                        startActivity(im);
                    } else {

                    }*/
                    proposalInput.show();

                    send_proposal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Proposal prop = new Proposal();
                            prop.recieverUid = user.getUid();
                            prop.senderUid = HomeActivity.currentUser.getUid();
                            prop.proposalText = proposal.getText().toString().trim();
                            proposalInput.dismiss();
                        }
                    });
                    cancel_proposal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            proposalInput.dismiss();
                        }
                    });
                }
            });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });


        return mainDialog;
    }

    void intialize_views() {
        name = (ourTextView) mainDialog.findViewById(R.id.card_name);
        cancel_proposal = (Button) proposalDialog.findViewById(R.id.cancel_proposal_editing);
        send_proposal = (Button) proposalDialog.findViewById(R.id.sendProposal);
        proposal = (EditText) proposalDialog.findViewById(R.id.proposal);
        phone = (ourTextView) mainDialog.findViewById(R.id.card_phone);
        goingTo = (ourTextView) mainDialog.findViewById(R.id.card_destination);
        fblink = (ImageView) mainDialog.findViewById(R.id.card_fb);
        call = (ImageView) mainDialog.findViewById(R.id.card_call);
        message = (ImageView) mainDialog.findViewById(R.id.card_sms);
        user_image = (ImageView) mainDialog.findViewById(R.id.card_image);
        close = (ImageView) mainDialog.findViewById(R.id.card_close);

    }

    class Proposal {

        public String proposalText, senderUid, recieverUid;

        Proposal() {
            proposalText = "";
            senderUid = "";
            recieverUid = "";
        }
    }
}
