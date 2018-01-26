package com.app.rideshare.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.app.rideshare.utils.TypefaceUtils;


public class CheckBoxRegular extends AppCompatCheckBox {

    public CheckBoxRegular(Context context) {
        super(context);
        init();
    }

    public CheckBoxRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckBoxRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(TypefaceUtils.getOpenSansRegular(getContext()));
        }
    }
}