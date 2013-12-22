package com.waldm.proverbica.retriever;

import android.content.Context;
import android.util.Log;

import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.AsyncRetriever.CallbackHandler;
import com.waldm.proverbica.settings.SettingsManager;

public class WebSayingRetriever implements SayingRetriever, CallbackHandler {
    private static final String TAG = WebSayingRetriever.class.getSimpleName();

    private final Context context;
    private final SayingDisplayer sayingDisplayer;
    private AsyncRetriever asyncTask;

    public WebSayingRetriever(Context context, SayingDisplayer sayingDisplayer) {
        this.context = context;
        this.sayingDisplayer = sayingDisplayer;
        asyncTask = new AsyncRetriever(this);
    }

    @Override
    public void loadSaying(SayingSource sayingSource) {
        if (NetworkConnectivity.isNetworkAvailable(context) && !SettingsManager.getPrefAlwaysUseFile(context)
                && sayingSource != SayingSource.FILE) {
            Log.d(TAG, "Loading saying from the internet");
            asyncTask.execute();
        } else {
            new FileSayingRetriever(context, sayingDisplayer).loadSaying(sayingSource);
        }
    }

    @Override
    public void alertSayingLoaded(Saying saying) {
        asyncTask = new AsyncRetriever(this);
        sayingDisplayer.setSaying(saying);
    }
}
