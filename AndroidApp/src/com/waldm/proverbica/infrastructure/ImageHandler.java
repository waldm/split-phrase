package com.waldm.proverbica.infrastructure;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

public class ImageHandler {
    private static final String TAG = ImageHandler.class.getSimpleName();

    private final Context context;
    private Target target;

    public ImageHandler(Context context) {
        Log.d(TAG, "ImageHandler created");
        this.context = context;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    private RequestCreator getImage(String imageName) {
        return Picasso.with(context).load(imageName);
    }

    public void loadNextImage(String imageLocation) {
        Preconditions.checkNotNull(target, "Must call setTarget before loading image");
        getImage(imageLocation).into(target);
    }

    public void loadNextImage(String imageLocation, int width, int height) {
        Preconditions.checkNotNull(target, "Must call setTarget before loading image");
        Log.d(TAG, "Loading and resizing image to " + width + " x " + height);
        getImage(imageLocation).resize(width, height).into(target);
    }
}
