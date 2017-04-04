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
import android.widget.Button;

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
 * Created by Prateek on 03-04-2017.
 */

public class HailRequestsSent extends Fragment {
    View mainView;
    RecyclerView listofproposals;
    ArrayList<Proposal> list;
    ProposalRecycler pr;
    ourTextView noprop;
    Proposal temp;
    Button clearall;
    RecyclerView.LayoutManager llm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.requests_sent, null);
        listofproposals = (RecyclerView) mainView.findViewById(R.id.sentRequestsRecyclerView);
        clearall = (Button) mainView.findViewById(R.id.clearAll);
        llm = new LinearLayoutManager(getContext());
        list = new ArrayList<>();
        noprop = (ourTextView) mainView.findViewById(R.id.noSentRequestsBanner);
        pr = new ProposalRecycler(list, getContext(), getActivity().getLayoutInflater(), listofproposals, true);
        listofproposals.setAdapter(pr);
        listofproposals.setLayoutManager(llm);

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        HomeActivity.messagesReference.child(HomeActivity.currentUser.getUid()).child("sent").addChildEventListener(new ChildEventListener() {
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
        clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.messagesReference.child(HomeActivity.currentUser.getUid()).child("sent").removeValue();
                list.clear();
                listofproposals.invalidate();
                listofproposals.setAdapter(pr);
            }
        });
    }
}
