package com.app.rideWhiz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareRelativeView extends LinearLayout {

    public SquareRelativeView(final Context context) {
        super(context);
    }

    public SquareRelativeView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRelativeView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        /*final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        setMeasuredDimension(width, width);*/
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }
}