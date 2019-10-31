package com.app.rideWhiz.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.app.rideWhiz.utils.TypefaceUtils;


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