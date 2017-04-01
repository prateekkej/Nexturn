package com.nexturn.ModifiedViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Prateek on 18-03-2017.
 */

public class ourTextView extends android.support.v7.widget.AppCompatTextView {

public ourTextView(Context ct,AttributeSet attributeSet)
{
 super(ct,attributeSet);
    Typeface tf = Typeface.createFromAsset(ct.getAssets(), "fonts/Raleway-Light.ttf");
    this.setTypeface(tf);
}
}
