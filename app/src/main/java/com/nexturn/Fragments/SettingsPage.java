package com.nexturn.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexturn.R;

/**
 * Created by Prateek on 25-03-2017.
 */

public class SettingsPage extends android.support.v4.app.Fragment {
    private View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.settings_page, null);
        return mainView;
    }

}
