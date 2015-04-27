package com.waldm.proverbica.infrastructure;

import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.waldm.proverbica.R;

public class ImageHandler {
    private static final String TAG = ImageHandler.class.getSimpleName();

    protected static final ImmutableList<Integer> imageResourceIds = new ImmutableList.Builder<Integer>()
            .add(R.drawable.lion).add(R.drawable.dog).build();
    private final Context context;
    private Target target;

    public ImageHandler(Context context) {
        Log.d(TAG, "ImageHandler created");
        this.context = context;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void loadNextImage(int width, int height) {
        Preconditions.checkNotNull(target, "Must call setTarget before loading image");
        Log.d(TAG, "Loading and resizing image to " + width + " x " + height);
        Picasso.with(context).load(imageResourceIds.get(new Random().nextInt(imageResourceIds.size())))
                .resize(width, height).into(target);
    }
}
