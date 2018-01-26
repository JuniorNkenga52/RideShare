package com.app.rideshare.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.app.rideshare.utils.TypefaceUtils;


public class TextViewItalic extends AppCompatTextView {

    public TextViewItalic(Context context) {
        super(context);
        init();
    }

    public TextViewItalic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewItalic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            setTypeface(TypefaceUtils.getOpenSansItalic(getContext()));
        }
    }
}