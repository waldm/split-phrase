package com.waldm.proverbica;

import android.graphics.Bitmap;

public interface SayingDisplayer {
    void displaySaying(Saying currentSaying, Bitmap bitmap, boolean canGoBack);
}
