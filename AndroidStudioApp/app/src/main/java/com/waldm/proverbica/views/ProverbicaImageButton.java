package com.waldm.proverbica.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ProverbicaImageButton extends ImageView implements ProverbicaButton {
    public ProverbicaImageButton(Context context) {
        super(context);
    }

    public ProverbicaImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProverbicaImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setBackgroundTextAndAlpha(int backgroundResource, float alpha, int textResource) {
        setBackgroundAndText(backgroundResource, textResource);
        setAlpha(alpha);
    }

    @Override
    public void setBackgroundAndText(int backgroundResource, int textResource) {
        setImageResource(backgroundResource);
    }
}
