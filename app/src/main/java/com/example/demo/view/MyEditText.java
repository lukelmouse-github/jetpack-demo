package com.example.demo.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import com.drake.logcat.LogCat;

public class MyEditText extends EditText {
    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogCat.e("onDetachedFromWindow");
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogCat.e("onAttachedToWindow");
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        LogCat.e(this.toString() + " onFocusChanged: " + focused);
    }
}
