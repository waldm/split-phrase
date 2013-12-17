package com.waldm.proverbica.retriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.settings.SettingsFragment;

public class WebSayingRetriever extends AsyncTask<Void, Void, Saying> implements SayingRetriever {

    private static final String WEBSITE = "http://proverbica.herokuapp.com/";
    private static final String BACKGROUND_PAGE = "http://proverbica.com/bg";
    private static final String SAYING_PAGE = WEBSITE + "saying";
    private static final String TAG = WebSayingRetriever.class.getSimpleName();
    private final Context context;
    private final SayingDisplayer sayingDisplayer;

    public WebSayingRetriever(Context context, SayingDisplayer sayingDisplayer) {
        this.context = context;
        this.sayingDisplayer = sayingDisplayer;
    }

    @Override
    protected Saying doInBackground(Void... v) {
        return new Saying(loadSayingText(SayingSource.EITHER), loadImageLocation());
    }

    private String readWebpage(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet, localContext);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "";

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Saying saying) {
        super.onPostExecute(saying);
        Log.d(TAG, "Loaded saying from the internet");
        sayingDisplayer.setSaying(saying);
    }

    @Override
    public SayingRetriever loadSayingAndRefresh(SayingSource sayingSource) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean alwaysUseFile = sharedPref.getBoolean(SettingsFragment.KEY_PREF_ALWAYS_USE_FILE, false);

        if (NetworkConnectivity.isNetworkAvailable(context) && !alwaysUseFile && sayingSource != SayingSource.FILE) {
            Log.d(TAG, "Loading saying from the internet");
            this.execute();
            return new WebSayingRetriever(context, sayingDisplayer);
        } else {
            return new FileSayingRetriever(context, sayingDisplayer).loadSayingAndRefresh(sayingSource);
        }
    }

    private String loadSayingText(SayingSource sayingSource) {
        return readWebpage(SAYING_PAGE);
    }

    private String loadImageLocation() {
        return WebSayingRetriever.WEBSITE + readWebpage(BACKGROUND_PAGE);
    }

    @Override
    public Saying loadSaying(SayingSource sayingSource) {
        return new Saying(loadSayingText(sayingSource), loadImageLocation());
    }
}
