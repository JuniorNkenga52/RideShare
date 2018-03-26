package com.app.rideshare.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.app.rideshare.utils.TypefaceUtils;


public class RadioButtonRegular extends AppCompatRadioButton {

    public RadioButtonRegular(Context context) {
        super(context);
        init();
    }

    public RadioButtonRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadioButtonRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(TypefaceUtils.getOpenSansRegular(getContext()));
        }
    }
}