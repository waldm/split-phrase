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
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.infrastructure.ImageHandler;
import com.waldm.proverbica.retriever.FileSayingRetriever;

public class UpdateWidgetService extends Service implements SayingDisplayer, Target {
    private static final String TAG = UpdateWidgetService.class.getSimpleName();

    private static final int MAXIMUM_SAYING_LENGTH = 55;

    private ImageHandler imageHandler;
    private RemoteViews remoteViews;
    private String text;
    private int[] allWidgetIds;

    public UpdateWidgetService() {
        imageHandler = new ImageHandler(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "Starting service");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {
            Log.d(TAG, "Widget id: " + widgetId);
            remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),
                    R.layout.widget_provider_layout);
            // Set the text
            FileSayingRetriever sayingRetriever = new FileSayingRetriever(this, this);

            text = sayingRetriever.loadSaying();
            while (text.length() > MAXIMUM_SAYING_LENGTH) {
                text = sayingRetriever.loadSaying();
            }

            imageHandler.setTarget(this);
            imageHandler.loadImage(imageHandler.getNextImage(), 300, 200);

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(), WidgetProvider.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.text_box, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
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
    public void setText(String result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
        Log.d(TAG, "Loading image");
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
        Log.d(TAG, "Image loaded");
        remoteViews.setImageViewBitmap(R.id.image, bitmap);
        remoteViews.setTextViewText(R.id.text_box, text);
        updateUI();
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
}