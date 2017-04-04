package com.nexturn.ModifiedViews;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nexturn.Fragments.HailRequests;
import com.nexturn.Fragments.HailRequestsSent;
import com.nexturn.Fragments.Profile_Fragment;

/**
 * Created by Prateek on 01-04-2017.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Profile_Fragment();
            case 1:
                return new HailRequests();
            case 2:
                return new HailRequestsSent();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Profile";
            case 1:
                return "Received Requests";
            case 2:
                return "Sent Requests";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
