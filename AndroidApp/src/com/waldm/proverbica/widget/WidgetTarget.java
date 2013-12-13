package com.waldm.proverbica.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.waldm.proverbica.R;

public class WidgetTarget implements Target {
    private static final String TAG = WidgetTarget.class.getSimpleName();
    private final RemoteViews remoteViews;
    private final Context context;
    private String text;

    public WidgetTarget(RemoteViews remoteViews, Context context) {
        this.remoteViews = remoteViews;
        this.context = context;
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
        Log.d(TAG, "Loading image");
        remoteViews.setTextViewText(R.id.text_box, context.getString(R.string.loading_proverb));
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
        Log.d(TAG, "Image loaded");
        remoteViews.setImageViewBitmap(R.id.image, bitmap);
        remoteViews.setTextViewText(R.id.text_box, text);
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
        Log.d(TAG, "Image failed to load");
        remoteViews.setTextViewText(R.id.text_box, context.getString(R.string.failed_to_load_proverb));
    }

    public void setText(String text) {
        this.text = text;
    }
}
