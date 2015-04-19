package com.waldm.proverbica;

import android.graphics.Bitmap;

public interface SayingDisplayer {

    void setSaying(Saying saying);

    void updateFavouritesButton(float alpha);

    void updateShareIntent();

    void displaySaying(Saying currentSaying, Bitmap bitmap, boolean canGoBack);
}
