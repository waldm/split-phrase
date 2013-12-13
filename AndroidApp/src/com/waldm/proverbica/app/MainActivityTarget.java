package com.waldm.proverbica.app;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.waldm.proverbica.R;

public class MainActivityTarget implements Target {
    private static final String TAG = MainActivityTarget.class.getSimpleName();

    private final TextView textView;
    private final ImageView imageView;
    private String text;

    public MainActivityTarget(TextView textView, ImageView imageView) {
        this.textView = textView;
        this.imageView = imageView;
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
        Log.d(TAG, "Loading image");
        textView.setText(R.string.loading_proverb);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
        Log.d(TAG, "Image loaded");
        imageView.setImageBitmap(bitmap);
        textView.setText(text);
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
        Log.d(TAG, "Image failed to load");
        textView.setText(R.string.failed_to_load_proverb);
    }

    public void setText(String text) {
        this.text = text;
    }
};
