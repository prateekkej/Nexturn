package com.nexturn;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nexturn.Activites.HomeActivity;
import com.nexturn.ModifiedViews.Proposal;
import com.nexturn.ModifiedViews.ourTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prateek on 03-04-2017.
 */

public class ProposalRecycler extends RecyclerView.Adapter<ProposalRecycler.myViewHolder> {
    Context context;
    AlertDialog alertDialog;
    ImageView userImageBig;
    Button accept, reject;
    boolean sentOrRecieved;
    View x;
    RecyclerView recyclerView;
    private ArrayList<Proposal> data;
    private Proposal prop;
    private ourTextView name, phone, goingTo, proposal;

    public ProposalRecycler(ArrayList<Proposal> e, Context ct, LayoutInflater inflater, RecyclerView recyclerView, boolean sentOrRecieved) {
        super();
        this.sentOrRecieved = sentOrRecieved;
        data = e;
        this.recyclerView = recyclerView;
        context = ct;
        x = inflater.inflate(R.layout.proposal_card, null);
        accept = (Button) x.findViewById(R.id.acceptProposal);
        reject = (Button) x.findViewById(R.id.rejectProposal);
        name = (ourTextView) x.findViewById(R.id.proposer_name);
        userImageBig = (ImageView) x.findViewById(R.id.userImage_proposal_large);
        phone = (ourTextView) x.findViewById(R.id.proposer_phone);
        goingTo = (ourTextView) x.findViewById(R.id.proposer_destination);
        proposal = (ourTextView) x.findViewById(R.id.protext);
        alertDialog = new AlertDialog.Builder(context).create();
    }

    @Override
    public ProposalRecycler.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hail_message_layout, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void onBindViewHolder(final ProposalRecycler.myViewHolder holder, final int position) {
        final Proposal temp = new Proposal(data.get(position));
        if (!sentOrRecieved) {
            holder.name.setText(temp.senderName);
            Log.d("PRop", temp.proposalText);
            holder.proposal.setText(temp.proposalText);
            Glide.with(context).load(temp.senderIMG).asBitmap().into(new BitmapImageViewTarget(holder.userImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.userImage.setImageDrawable(circularBitmapDrawable);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name.setText(temp.senderName);
                    phone.setText(temp.senderPhone);
                    goingTo.setText(temp.senderDest);
                    proposal.setText(temp.proposalText);
                    Glide.with(context).load(temp.senderIMG).asBitmap().into(new BitmapImageViewTarget(userImageBig) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            userImageBig.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Proposal temp2 = new Proposal(temp);
                            temp2.senderName = HomeActivity.user_location.name;
                            temp2.senderPhone = HomeActivity.user_location.phone;
                            temp2.proposalText = "Accepted";
                            temp2.senderUid = temp.recieverUid;
                            temp2.recieverUid = temp.senderUid;
                            temp2.senderIMG = HomeActivity.user_obj.imgURL;
                            temp2.senderFB = HomeActivity.user_location.fblink;
                            HomeActivity.messagesReference.child(temp2.recieverUid).child("recieved").child(temp2.key).setValue(temp2);
                            alertDialog.dismiss();

                        }
                    });
                    reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            HomeActivity.messagesReference.child(HomeActivity.currentUser.getUid()).child("recieved").child(temp.key).removeValue();
                            recyclerView.removeViewAt(position);
                            data.remove(position);
                        }
                    });
                    alertDialog.setView(x);
                    alertDialog.show();
                }
            });


        } else {
            holder.name.setText(temp.recieverName);
            holder.proposal.setText(temp.proposalText);
            Glide.with(context).load(temp.recieverImage).asBitmap().into(new BitmapImageViewTarget(holder.userImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.userImage.setImageDrawable(circularBitmapDrawable);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context).setTitle("Delete Sent Proposal").setMessage("The proposal will be deleted from your history only")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    HomeActivity.messagesReference.child(HomeActivity.currentUser.getUid()).child("sent").child(temp.key).removeValue();
                                    recyclerView.removeViewAt(position);
                                    data.remove(position);
                                }
                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();


                    return true;
                }
            });
        }


    }

    class myViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public ourTextView name, proposal;

        public myViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.userImageProposalMini);
            proposal = (ourTextView) itemView.findViewById(R.id.proposal);
            name = (ourTextView) itemView.findViewById(R.id.other_party_name);

        }
    }


}
