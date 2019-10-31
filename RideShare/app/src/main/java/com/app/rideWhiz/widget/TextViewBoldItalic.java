package com.app.rideWhiz.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.app.rideWhiz.utils.TypefaceUtils;


public class TextViewBoldItalic extends AppCompatTextView {

    public TextViewBoldItalic(Context context) {
        super(context);
        init();
    }

    public TextViewBoldItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewBoldItalic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(TypefaceUtils.getOpenSansBoldItalic(getContext()));
        }
    }
}