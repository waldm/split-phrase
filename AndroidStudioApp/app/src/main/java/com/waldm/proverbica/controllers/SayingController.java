package com.waldm.proverbica.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;

import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.SayingListener;
import com.waldm.proverbica.infrastructure.ImageHandler;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.SayingRetriever;
import com.waldm.proverbica.settings.SettingsManager;

import java.util.List;

public class SayingController implements Target, SayingListener {
    private static final String TAG = SayingController.class.getSimpleName();
    private final Context context;
    private final List<Pair<Saying, Bitmap>> sayings = Lists.newArrayList();
    private Saying tempSaying;
    private int currentSayingIndex = -1;
    private final ImageHandler imageHandler;
    private final SayingDisplayer sayingDisplayer;
    private SayingRetriever sayingRetriever;

    public SayingController(Context context, SayingDisplayer sayingDisplayer, SayingRetriever sayingRetriever){
        this.imageHandler = new ImageHandler(context);
        imageHandler.setTarget(this);
        this.context = context;
        this.sayingDisplayer = sayingDisplayer;
        setSayingRetriever(sayingRetriever);
    }

    @Override
    public void setSaying(Saying saying) {
        this.tempSaying = saying;
        imageHandler.loadImage(saying.getImageLocation());
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
        currentSayingIndex++;
        sayingDisplayer.displaySaying(tempSaying, bitmap, currentSayingIndex > 0);
        sayings.add(new Pair<>(tempSaying, bitmap));
        tempSaying = null;
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
        Log.e(TAG, "onBitmapFailed");
        Log.d(TAG, "Image failed to load");
        currentSayingIndex++;
        sayingDisplayer.displaySaying(tempSaying, null, currentSayingIndex > 0);
        sayings.add(new Pair<Saying, Bitmap>(tempSaying, null));
        tempSaying = null;
    }

    public Saying getCurrentSaying() {
        if (currentSayingIndex >= sayings.size() || currentSayingIndex < 0){
            return null;
        }else {
            return sayings.get(currentSayingIndex).first;
        }
    }

    public void loadSaying(SayingSource source, ImageSize imageSize) {
        sayingRetriever.loadSaying(source, imageSize);
    }

    public void setSayingRetriever(SayingRetriever sayingRetriever) {
        this.sayingRetriever = sayingRetriever;
        sayingRetriever.setSayingListener(this);
    }

    public void displayNextSaying() {
        if (currentSayingIndex == sayings.size() - 1){
            sayingRetriever.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
        }else{
            currentSayingIndex++;
            Pair<Saying, Bitmap> saying = sayings.get(currentSayingIndex);
            sayingDisplayer.displaySaying(saying.first, saying.second, true);
        }
    }

    public void displayPreviousSaying() {
        if (currentSayingIndex == 0){
            throw new UnsupportedOperationException("No previous saying to go to");
        } else {
            currentSayingIndex--;
            Pair<Saying, Bitmap> saying = sayings.get(currentSayingIndex);
            sayingDisplayer.displaySaying(saying.first, saying.second, currentSayingIndex > 0);
        }
    }
}
