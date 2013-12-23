package com.waldm.proverbica.views;

import android.view.View.OnClickListener;

public interface ProverbicaButton {
    void setBackgroundTextAndAlpha(int backgroundResource, float alpha, int textResource);

    void setOnClickListener(OnClickListener onClickListener);

    void setAlpha(float f);
}
