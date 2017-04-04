package com.nexturn.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexturn.Activites.HomeActivity;
import com.nexturn.ModifiedViews.MyPagerAdapter;
import com.nexturn.R;

/**
 * Created by Prateek on 01-04-2017.
 */

public class Promesride extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.promesride, null, false);
        ViewPager vp = (ViewPager) v.findViewById(R.id.promesride_viewPager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        vp.setAdapter(pagerAdapter);
        vp.setOffscreenPageLimit(3);
        TabLayout tb = (TabLayout) v.findViewById(R.id.tab_promeside);
        tb.setupWithViewPager(vp);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    HomeActivity.actionBar.setTitle("Profile");
                } else if (position == 1) {

                    HomeActivity.actionBar.setTitle("Requests");
                }

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    HomeActivity.actionBar.setTitle("Profile");
                } else {
                    HomeActivity.actionBar.setTitle("Requests");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return v;
    }


}
