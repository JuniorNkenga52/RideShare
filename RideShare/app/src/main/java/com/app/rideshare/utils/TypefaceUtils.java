package com.app.rideshare.utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceUtils {

    private static Typeface mRobotoMediam;
    private static Typeface OpenSansLight;
    private static Typeface OpenSansRegular;
    private static Typeface OpenSansLightItalic;
    private static Typeface OpenSansBold;
    private static Typeface OpenSansSemibold;
    private static Typeface OpenSansExtrabold;
    private static Typeface OpenSansSemiBoldItalic;
    private static Typeface OpenSansBoldItalic;
    private static Typeface OpenSansExtraBoldItalic;
    private static Typeface OpenSansItalic;

    public static Typeface getTypefaceRobotoMediam(Context context) {

        if (mRobotoMediam == null)
            mRobotoMediam = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        return mRobotoMediam;
    }


    public static Typeface getOpenSansLight(Context context) {

        if (OpenSansLight == null)
            OpenSansLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

        return OpenSansLight;
    }

    public static Typeface getOpenSansLightItalic(Context context) {

        if (OpenSansLightItalic == null)
            OpenSansLightItalic = Typeface.createFromAsset(context.getAssets(), "OpenSans-LightItalic.ttf");

        return OpenSansLightItalic;
    }

    public static Typeface getOpenSansRegular(Context context) {

        if (OpenSansRegular == null)
            OpenSansRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");

        return OpenSansRegular;
    }

    public static Typeface getOpenSansSemibold(Context context) {

        if (OpenSansSemibold == null)
            OpenSansSemibold = Typeface.createFromAsset(context.getAssets(), "OpenSans-SemiBold.ttf");

        return OpenSansSemibold;
    }

    public static Typeface getOpenSansSemiBoldItalic(Context context) {

        if (OpenSansSemiBoldItalic == null)
            OpenSansSemiBoldItalic = Typeface.createFromAsset(context.getAssets(), "OpenSans-SemiBoldItalic.ttf");

        return OpenSansSemiBoldItalic;
    }

    public static Typeface getOpenSansBold(Context context) {

        if (OpenSansBold == null)
            OpenSansBold = Typeface.createFromAsset(context.getAssets(), "OpenSans-Bold.ttf");

        return OpenSansBold;
    }

    public static Typeface getOpenSansBoldItalic(Context context) {

        if (OpenSansBoldItalic == null)
            OpenSansBoldItalic = Typeface.createFromAsset(context.getAssets(), "OpenSans-BoldItalic.ttf");

        return OpenSansBoldItalic;
    }

    public static Typeface getOpenSansExtraBold(Context context) {

        if (OpenSansExtrabold == null)
            OpenSansExtrabold = Typeface.createFromAsset(context.getAssets(), "OpenSans-ExtraBold.ttf");

        return OpenSansExtrabold;
    }

    public static Typeface getOpenSansExtraBoldItalic(Context context) {

        if (OpenSansExtraBoldItalic == null)
            OpenSansExtraBoldItalic = Typeface.createFromAsset(context.getAssets(), "OpenSans-ExtraBoldItalic.ttf");

        return OpenSansExtraBoldItalic;
    }

    public static Typeface getOpenSansItalic(Context context) {

        if (OpenSansItalic == null)
            OpenSansItalic = Typeface.createFromAsset(context.getAssets(), "OpenSans-Italic.ttf");

        return OpenSansItalic;
    }
}