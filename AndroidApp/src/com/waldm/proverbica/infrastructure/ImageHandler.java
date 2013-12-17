package com.waldm.proverbica.infrastructure;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.waldm.proverbica.retriever.WebSayingRetriever;
import com.waldm.proverbica.settings.SettingsFragment;

public class ImageHandler {
    private static final String TAG = ImageHandler.class.getSimpleName();

    private int imageIndex;
    protected static final String[] images = { "lion.jpg", "monkey.jpg", "gorilla.jpg", "hawk.jpg", "owl.jpg",
            "dog.jpg", "tiger.jpg", "polar_bear.jpg", "elephant.jpg", "leopard.jpg", "cat.jpg" };
    private static final String IMAGES_DIR = WebSayingRetriever.WEBSITE + "images/";
    private final Context context;
    private Target target;

    public ImageHandler(Context context) {
        Log.d(TAG, "ImageHandler created");
        this.context = context;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    private String getNextImage() {
        int newImageIndex = new Random().nextInt(images.length);
        while (newImageIndex == imageIndex) {
            newImageIndex = new Random().nextInt(images.length);
        }

        imageIndex = newImageIndex;
        return images[imageIndex];
    }

    private RequestCreator getImage(String imageName) {
        return Picasso.with(context).load(imageName);
    }

    private RequestCreator loadImageFromWeb(String imageName) {
        Log.d(TAG, "Loading image from web");
        return getImage(IMAGES_DIR + imageName);
    }

    private RequestCreator loadImageFromFile(String imageName) {
        Log.d(TAG, "Loading image from file");
        return getImage("file:///android_asset/backgrounds/" + imageName);
    }

    private RequestCreator loadImageFromSource(String imageName) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean alwaysUseFile = sharedPref.getBoolean(SettingsFragment.KEY_PREF_ALWAYS_USE_FILE, false);

        if (NetworkConnectivity.isNetworkAvailable(context) && !alwaysUseFile) {
            return loadImageFromWeb(imageName);
        } else {
            return loadImageFromFile(imageName);
        }
    }

    public void loadNextImage() {
        Preconditions.checkNotNull(target, "Must call setTarget before loading image");
        loadImageFromSource(getNextImage()).into(target);
    }

    public void loadNextImage(int width, int height) {
        Preconditions.checkNotNull(target, "Must call setTarget before loading image");
        Log.d(TAG, "Loading and resizing image to " + width + " x " + height);
        loadImageFromSource(getNextImage()).resize(width, height).into(target);
    }
}
