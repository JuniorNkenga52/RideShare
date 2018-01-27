package com.app.rideshare.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.app.rideshare.utils.TypefaceUtils;


public class TextViewSemiBoldItalic extends AppCompatTextView {

    public TextViewSemiBoldItalic(Context context) {
        super(context);
        init();
    }

    public TextViewSemiBoldItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewSemiBoldItalic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(TypefaceUtils.getOpenSansSemiBoldItalic(getContext()));
        }
    }
}