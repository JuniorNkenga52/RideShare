package com.app.rideWhiz.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.app.rideWhiz.utils.TypefaceUtils;


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