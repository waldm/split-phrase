package com.waldm.proverbica.retriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.settings.SettingsManager;

public class FileSayingRetriever implements SayingRetriever {
    private static final String TAG = FileSayingRetriever.class.getSimpleName();

    private static final String IMAGES_DIR = "file:///android_asset/backgrounds/";
    private static final String FILENAME = "sayings.txt";
    private List<String> sayings;
    private final Context context;
    private final SayingDisplayer sayingDisplayer;
    private String[] backgrounds;
    private String currentBackground;

    public FileSayingRetriever(Context context, SayingDisplayer sayingDisplayer) {
        this.context = context;
        this.sayingDisplayer = sayingDisplayer;
    }

    @Override
    public SayingRetriever loadSayingAndRefresh(SayingSource sayingSource) {
        if (NetworkConnectivity.isNetworkAvailable(context) && !SettingsManager.getPrefAlwaysUseFile(context)
                && sayingSource != SayingSource.FILE) {
            return new WebSayingRetriever(context, sayingDisplayer).loadSayingAndRefresh(sayingSource);
        } else {
            sayingDisplayer.setSaying(loadSaying(sayingSource));
            return this;
        }
    }

    private String loadImageLocation() {
        if (backgrounds == null) {
            try {
                backgrounds = context.getAssets().list("backgrounds");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                System.exit(1);
            }
        }

        int newImageIndex = new Random().nextInt(backgrounds.length);
        if (currentBackground != null) {
            while (backgrounds[newImageIndex].equals(currentBackground)) {
                newImageIndex = new Random().nextInt(backgrounds.length);
            }
        }

        currentBackground = backgrounds[newImageIndex];

        return IMAGES_DIR + currentBackground;
    }

    private String loadSayingText(SayingSource sayingSource) {
        if (NetworkConnectivity.isNetworkAvailable(context) && !SettingsManager.getPrefAlwaysUseFile(context)
                && sayingSource != SayingSource.FILE) {
            return new WebSayingRetriever(context, sayingDisplayer).loadSaying(sayingSource).getText();
        } else {
            Log.d(TAG, "Loading saying from file");
            if (sayings == null) {
                Log.d(TAG, "Loading all sayings from file");
                sayings = new ArrayList<String>();

                InputStream stream = null;
                try {
                    stream = context.getAssets().open(FILENAME);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String saying;
                try {
                    while ((saying = reader.readLine()) != null) {
                        sayings.add(saying);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Random random = new Random();
            String beginning = sayings.get(random.nextInt(sayings.size())).split("\\|")[0];
            String end = sayings.get(random.nextInt(sayings.size())).split("\\|")[1];

            Log.d(TAG, "Loaded saying from file");
            return beginning + " " + end;
        }
    }

    @Override
    public Saying loadSaying(SayingSource sayingSource) {
        return new Saying(loadSayingText(sayingSource), loadImageLocation());
    }
}
