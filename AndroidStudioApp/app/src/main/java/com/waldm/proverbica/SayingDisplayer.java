package com.waldm.proverbica;

import android.graphics.Bitmap;

public interface SayingDisplayer {
    void updateFavouritesButton(float alpha);

    void updateShareIntent();

    void displaySaying(Saying currentSaying, Bitmap bitmap, boolean canGoBack);
}
