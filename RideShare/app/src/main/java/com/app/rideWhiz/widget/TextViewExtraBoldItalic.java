package com.app.rideWhiz.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.app.rideWhiz.utils.TypefaceUtils;


public class TextViewExtraBoldItalic extends AppCompatTextView {

    public TextViewExtraBoldItalic(Context context) {
        super(context);
        init();
    }

    public TextViewExtraBoldItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewExtraBoldItalic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(TypefaceUtils.getOpenSansExtraBoldItalic(getContext()));
        }
    }
}