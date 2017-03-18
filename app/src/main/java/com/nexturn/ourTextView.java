package com.nexturn;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Prateek on 18-03-2017.
 */

public class ourTextView extends TextView {

public ourTextView(Context ct,AttributeSet attributeSet)
{
 super(ct,attributeSet);
    Typeface tf= Typeface.createFromAsset(ct.getAssets(),"fonts/Raleway-Medium.ttf");
    this.setTypeface(tf);
}
}
