package com.nexturn.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.nexturn.Activites.HomeActivity;
import com.nexturn.ModifiedViews.Proposal;
import com.nexturn.ModifiedViews.ourTextView;
import com.nexturn.ProposalRecycler;
import com.nexturn.R;

import java.util.ArrayList;

/**
 * Created by Prateek on 02-04-2017.
 */

public class HailRequests extends Fragment {
    RecyclerView listofproposals;
    ArrayList<Proposal> list;
    ProposalRecycler pr;
    ourTextView noprop;
    Proposal temp;
    RecyclerView.LayoutManager llm;

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.messagesReference.child(HomeActivity.currentUser.getUid()).child("recieved").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                temp = new Proposal(dataSnapshot.getValue(Proposal.class));
                Log.d("PRop", dataSnapshot.toString());
                list.add(temp);
                if (list.isEmpty()) {
                    noprop.setVisibility(View.VISIBLE);
                    listofproposals.setVisibility(View.GONE);
                } else {
                    noprop.setVisibility(View.GONE);
                    listofproposals.setVisibility(View.VISIBLE);
                }
                listofproposals.invalidate();
                listofproposals.setAdapter(pr);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.requests_recieved, null);
        listofproposals = (RecyclerView) v.findViewById(R.id.recievedRequestsRecyclerView);
        llm = new LinearLayoutManager(getContext());
        list = new ArrayList<>();
        noprop = (ourTextView) v.findViewById(R.id.noHailRequestNotice);
        pr = new ProposalRecycler(list, getContext(), getActivity().getLayoutInflater(), listofproposals, false);
        listofproposals.setAdapter(pr);
        listofproposals.setLayoutManager(llm);


        return v;
    }
}
