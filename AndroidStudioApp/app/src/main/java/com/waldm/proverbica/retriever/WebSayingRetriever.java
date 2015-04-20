package com.waldm.proverbica.retriever;

import android.content.Context;
import android.util.Log;

import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.SayingListener;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.AsyncRetriever.CallbackHandler;
import com.waldm.proverbica.settings.SettingsManager;

public class WebSayingRetriever implements SayingRetriever, CallbackHandler {
    private static final String TAG = WebSayingRetriever.class.getSimpleName();

    private final Context context;
    private AsyncRetriever asyncTask;
    private SayingListener sayingListener;

    public WebSayingRetriever(Context context) {
        this.context = context;
        asyncTask = new AsyncRetriever(this);
    }

    @Override
    public void loadSaying(SayingSource sayingSource, ImageSize imageSize) {
        if (NetworkConnectivity.isNetworkAvailable(context) && !SettingsManager.getPrefAlwaysUseFile(context)
                && sayingSource != SayingSource.FILE) {
            Log.d(TAG, "Loading saying from the internet");
            asyncTask.execute();
        } else {
            new FileSayingRetriever(context).loadSaying(sayingSource, imageSize);
        }
    }

    @Override
    public void setSayingListener(SayingListener sayingListener) {
        this.sayingListener = sayingListener;
    }

    @Override
    public void alertSayingLoaded(Saying saying) {
        asyncTask = new AsyncRetriever(this);
        sayingListener.alertNewSaying(saying);
    }
}
