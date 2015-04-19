package com.waldm.proverbica.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.waldm.proverbica.R;
import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.app.MainActivity;
import com.waldm.proverbica.infrastructure.ImageHandler;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.FileSayingRetriever;

public class UpdateWidgetService extends Service implements SayingDisplayer, Target {
    private static final String TAG = UpdateWidgetService.class.getSimpleName();

    private static final int MAXIMUM_SAYING_LENGTH = 55;

    public static final String EXTRA_STARTED_VIA_WIDGET = "ExtraStartedViaWidget";

    private ImageHandler imageHandler;
    private RemoteViews remoteViews;
    private int[] allWidgetIds;
    private Saying saying;
    private final FileSayingRetriever sayingRetriever;

    public UpdateWidgetService() {
        imageHandler = new ImageHandler(this);
        sayingRetriever = new FileSayingRetriever(this, this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "Starting service");
        if (intent == null) {
            Log.d(TAG, "Intent is null, so returning early");
            return;
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {
            Log.d(TAG, "Widget id: " + widgetId);
            remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),
                    R.layout.widget_provider_layout);

            Intent textIntent = new Intent(this.getApplicationContext(), WidgetProvider.class);
            textIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            textIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            PendingIntent textPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, textIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.text_box, textPendingIntent);

            Intent imageIntent = new Intent(this.getApplicationContext(), MainActivity.class);
            imageIntent.putExtra(EXTRA_STARTED_VIA_WIDGET, true);
            PendingIntent imagePendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, imageIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.image, imagePendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        sayingRetriever.loadSaying(SayingSource.FILE, ImageSize.SMALL);

        Log.d(TAG, "Widget saying: " + saying.getText());
        Log.d(TAG, "Widget background: " + saying.getImageLocation());
        imageHandler.setTarget(this);
        imageHandler.loadImage(saying.getImageLocation());
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void setSaying(Saying saying) {
        if (saying.getText().length() > MAXIMUM_SAYING_LENGTH) {
            sayingRetriever.loadSaying(SayingSource.FILE, ImageSize.SMALL);
        } else {
            this.saying = saying;
        }
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
        Log.d(TAG, "Loading image");
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
        Log.d(TAG, "Image loaded");
        remoteViews.setImageViewBitmap(R.id.image, bitmap);
        remoteViews.setTextViewText(R.id.text_box, saying.getText());
        updateUI();
        SayingIO.writeSaying(saying, this);
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
        Log.d(TAG, "Image failed to load");
        remoteViews.setTextViewText(R.id.text_box, getString(R.string.failed_to_load_proverb));

        updateUI();
    }

    private void updateUI() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        for (int widgetId : allWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void updateFavouritesButton(float alpha) {
        // Widget has no favourites button
    }

    @Override
    public void updateShareIntent() {
        // Widget has no share button
    }

    @Override
    public void displaySaying(Saying currentSaying, Bitmap bitmap) {
        throw new UnsupportedOperationException();
    }
}