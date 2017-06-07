package com.app.rideshare.utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceUtils {

    private static Typeface mRobotoMediam;


    public static Typeface getTypefaceRobotoMediam(Context context) {

        if (mRobotoMediam == null)
            mRobotoMediam = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        return mRobotoMediam;
    }

}