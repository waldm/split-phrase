package com.waldm.proverbica.retriever;

import android.os.AsyncTask;
import android.util.Log;

import com.waldm.proverbica.Saying;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AsyncRetriever extends AsyncTask<Void, Void, Saying> {
    public static interface CallbackHandler {
        void alertSayingLoaded(Saying saying);
    }

    private static final String TAG = AsyncRetriever.class.getSimpleName();

    private static final String WEBSITE = "http://proverbica.herokuapp.com/";
    private static final String BACKGROUND_PAGE = "http://proverbica.com/bg";
    private static final String SAYING_PAGE = WEBSITE + "saying";
    private final CallbackHandler callback;

    public AsyncRetriever(CallbackHandler callback) {
        this.callback = callback;
    }

    @Override
    protected Saying doInBackground(Void... v) {
        return new Saying(loadSayingText(), loadImageLocation());
    }

    @Override
    protected void onPostExecute(Saying saying) {
        super.onPostExecute(saying);
        Log.d(TAG, "Loaded saying from the internet");
        callback.alertSayingLoaded(saying);
    }

    private String loadSayingText() {
        return readWebpage(SAYING_PAGE);
    }

    private String loadImageLocation() {
        return WEBSITE + readWebpage(BACKGROUND_PAGE);
    }

    private String readWebpage(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpClient.execute(httpGet, localContext);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String result = "";

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return null;
        }

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
