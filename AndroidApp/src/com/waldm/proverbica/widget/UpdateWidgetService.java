package com.waldm.proverbica.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.waldm.proverbica.ImageHandler;
import com.waldm.proverbica.R;
import com.waldm.proverbica.retriever.FileSayingRetriever;
import com.waldm.proverbica.retriever.SayingDisplayer;

public class UpdateWidgetService extends Service implements SayingDisplayer {
    private ImageHandler imageHandler;
    private String text;

    public UpdateWidgetService() {
        imageHandler = new ImageHandler(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),
                    R.layout.appwidget_provider_layout);
            // Set the text
            FileSayingRetriever sayingRetriever = new FileSayingRetriever(this, this);

            Target target = new Target() {
                @Override
                public void onPrepareLoad(Drawable arg0) {
                    remoteViews.setTextViewText(R.id.text_box, getString(R.string.loading_proverb));
                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
                    remoteViews.setImageViewBitmap(R.id.image, bitmap);
                    remoteViews.setTextViewText(R.id.text_box, text);
                }

                @Override
                public void onBitmapFailed(Drawable arg0) {
                    remoteViews.setTextViewText(R.id.text_box, getString(R.string.failed_to_load_proverb));
                }
            };

            text = sayingRetriever.loadSaying();
            imageHandler.setTarget(target);
            imageHandler.loadImage(imageHandler.getNextImage(), 300, 200);

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(), ExampleAppWidgetProvider.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.text_box, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();

        // super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void setText(String result) {
        // TODO Auto-generated method stub

    }
}