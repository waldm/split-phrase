package com.waldm.proverbica.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ProverbicaTextButton extends Button implements ProverbicaButton {
    public ProverbicaTextButton(Context context) {
        super(context);
    }

    public ProverbicaTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProverbicaTextButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setBackgroundTextAndAlpha(int backgroundResource, float alpha, int textResource) {
        setCompoundDrawablesWithIntrinsicBounds(backgroundResource, 0, 0, 0);
        setText(textResource);
    }
}
