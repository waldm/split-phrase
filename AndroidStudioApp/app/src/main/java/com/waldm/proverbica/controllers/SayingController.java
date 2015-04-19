package com.waldm.proverbica.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.waldm.proverbica.R;
import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.infrastructure.ImageHandler;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.settings.SettingsManager;

import java.util.List;

public class SayingController implements Target {
    private static final String TAG = SayingController.class.getSimpleName();
    private final Context context;
    private final List<Saying> sayings = Lists.newArrayList();
    private Saying currentSaying;
    private final ImageHandler imageHandler;
    private final SayingDisplayer sayingDisplayer;

    public SayingController(Context context, SayingDisplayer sayingDisplayer){
        this.imageHandler = new ImageHandler(context);
        imageHandler.setTarget(this);
        this.context = context;
        this.sayingDisplayer = sayingDisplayer;
    }

    public void setSaying(Saying saying) {
        this.currentSaying = saying;
        imageHandler.loadNextImage(saying.getImageLocation());
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
        Log.e(TAG, "onPrepareLoad");
        if (NetworkConnectivity.isNetworkAvailable(context) && !SettingsManager.getPrefAlwaysUseFile(context)) {
            Log.d(TAG, "Loading image");
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
        Log.e(TAG, "onBitmapLoaded");
        Log.d(TAG, "Image loaded");
        sayingDisplayer.displaySaying(currentSaying, bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
        Log.e(TAG, "onBitmapFailed");
        Log.d(TAG, "Image failed to load");
        sayingDisplayer.displaySaying(currentSaying, null);
    }

    public Saying getCurrentSaying() {
        return currentSaying;
    }
}
