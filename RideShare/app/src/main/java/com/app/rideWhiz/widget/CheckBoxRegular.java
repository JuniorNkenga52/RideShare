package com.app.rideWhiz.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.app.rideWhiz.utils.TypefaceUtils;


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