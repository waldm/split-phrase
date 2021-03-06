package com.waldm.proverbica.retriever;

import android.content.Context;
import android.util.Log;

import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingListener;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.settings.SettingsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileSayingRetriever implements SayingRetriever {
    private static final String TAG = FileSayingRetriever.class.getSimpleName();

    private static final String ASSETS_DIR = "file:///android_asset/";
    private static final String SMALL_SIZE_DIR = "widget_backgrounds";
    private static final String NORMAL_SIZE_DIR = "backgrounds";
    private static final String FILENAME = "sayings.txt";
    private List<String> sayings;
    private final Context context;
    private SayingListener sayingListener;
    private String[] backgrounds;
    private String currentBackground;

    public FileSayingRetriever(Context context) {
        this.context = context;
    }

    @Override
    public void loadSaying(SayingSource sayingSource, ImageSize imageSize) {
        if (NetworkConnectivity.isNetworkAvailable(context) && !SettingsManager.getPrefAlwaysUseFile(context)
                && sayingSource != SayingSource.FILE) {
            new WebSayingRetriever(context).loadSaying(sayingSource, imageSize);
        } else {
            sayingListener.alertNewSaying(new Saying(loadSayingText(sayingSource), loadImageLocation(imageSize)));
        }
    }

    @Override
    public void setSayingListener(SayingListener sayingListener) {
        this.sayingListener = sayingListener;
    }

    private String loadImageLocation(ImageSize imageSize) {
        String imageDir = null;
        switch (imageSize) {
            case NORMAL:
                imageDir = NORMAL_SIZE_DIR;
                break;
            case SMALL:
                imageDir = SMALL_SIZE_DIR;
                break;
            default:
                break;
        }

        if (backgrounds == null) {
            try {
                backgrounds = context.getAssets().list(imageDir);
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

        return ASSETS_DIR + imageDir + "/" + currentBackground;
    }

    private String loadSayingText(SayingSource sayingSource) {
        Log.d(TAG, "Loading saying from file");
        if (sayings == null) {
            Log.d(TAG, "Loading all sayings from file");
            sayings = new ArrayList<>();

            InputStream stream;
            try {
                stream = context.getAssets().open(FILENAME);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
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
