package com.waldm.proverbica.app;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ShareActionProvider;

import com.google.common.base.Stopwatch;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.waldm.proverbica.R;
import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.app.ShakeDetector.Callback;
import com.waldm.proverbica.favourites.FavouritesActivity;
import com.waldm.proverbica.favourites.FavouritesIO;
import com.waldm.proverbica.info.InfoActivity;
import com.waldm.proverbica.infrastructure.ImageHandler;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.FileSayingRetriever;
import com.waldm.proverbica.retriever.SayingRetriever;
import com.waldm.proverbica.retriever.WebSayingRetriever;
import com.waldm.proverbica.settings.SettingsActivity;
import com.waldm.proverbica.settings.SettingsManager;
import com.waldm.proverbica.views.CustomViewPager;
import com.waldm.proverbica.views.ProverbicaButton;
import com.waldm.proverbica.views.ViewPageAdapter;
import com.waldm.proverbica.widget.SayingIO;
import com.waldm.proverbica.widget.UpdateWidgetService;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener, SayingDisplayer, Target,
        Callback {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final float BUTTON_TRANSPARENCY = 0.3f;
    protected static final long SLIDESHOW_TRANSITION = 3000;
    private static final int BUTTON_HIDE_TIME = 2000;

    private static final String SLIDESHOW_RUNNING = "SLIDESHOW_RUNNING";
    private static final String SAYING_TEXT = "SayingText";
    private static final String SAYING_IMAGE = "SayingImage";
    private SayingRetriever sayingRetriever;
    private ImageHandler imageHandler;
    private CustomViewPager viewPager;
    private ShareActionProvider shareActionProvider;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean slideshowRunning;
    private ProverbicaButton slideShowButton;
    private ProverbicaButton favouritesButton;
    private Runnable hideFavouritesButton;
    private Runnable hideSlideshowButton;
    private Runnable moveToNextImage;
    private Stopwatch stopwatch = Stopwatch.createUnstarted();
    private List<String> favourites;
    private Menu menu;
    private ShakeDetector shakeDetector;
    private ViewPageAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.app_name));

        moveToNextImage = new Runnable() {
            @Override
            public void run() {
                if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > SLIDESHOW_TRANSITION) {
                    viewPager.moveToEnd();
                    stopwatch.reset();
                    stopwatch.start();
                }

                handler.postDelayed(moveToNextImage, SLIDESHOW_TRANSITION);
            }
        };

        imageHandler = new ImageHandler(this);
        imageHandler.setTarget(this);

        if (SettingsManager.getPrefAlwaysUseFile(this)) {
            sayingRetriever = new FileSayingRetriever(this, this);
        } else {
            sayingRetriever = new WebSayingRetriever(this, this);
        }

        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        viewPager.setFields(sayingRetriever, this);
        viewPageAdapter = new ViewPageAdapter(this);
        viewPager.setAdapter(viewPageAdapter);

        slideShowButton = (ProverbicaButton) findViewById(R.id.button_slideshow);
        favouritesButton = (ProverbicaButton) findViewById(R.id.button_favourite);

        addClickListeners();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (savedInstanceState == null) {
            if (!tryLoadSayingFromWidget(getIntent())) {
                sayingRetriever.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
            }
        } else {
            slideshowRunning = savedInstanceState.getBoolean(SLIDESHOW_RUNNING);
            setSaying(new Saying(savedInstanceState.getString(SAYING_TEXT), savedInstanceState.getString(SAYING_IMAGE)));

            if (slideshowRunning) {
                startSlideshow();
            }
        }
    }

    private boolean tryLoadSayingFromWidget(Intent intent) {
        if (intent.getBooleanExtra(UpdateWidgetService.EXTRA_STARTED_VIA_WIDGET, false)) {
            // Load saying currently being shown in widget
            Saying saying = SayingIO.readSaying(this);
            if (saying != null) {
                viewPageAdapter = new ViewPageAdapter(this);
                viewPager.setAdapter(viewPageAdapter);
                setSaying(saying);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tryLoadSayingFromWidget(intent);
    }

    private void addClickListeners() {
        slideShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideShowButton.setAlpha(1f);
                if (MainActivity.this.slideshowRunning) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    stopSlideshow();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    startSlideshow();
                }

                handler.removeCallbacks(hideSlideshowButton);
                hideButtons(BUTTON_HIDE_TIME);
            }
        });

        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSayingIsFavourited();

                updateFavouritesMenuItemDrawable();

                updateFavouritesButton(1);

                handler.removeCallbacks(hideFavouritesButton);
                hideButtons(BUTTON_HIDE_TIME);
                FavouritesIO.writeFavourites(favourites, MainActivity.this);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stopwatch.isRunning()) {
            stopwatch.stop();
        }

        handler.removeCallbacks(hideSlideshowButton);
        handler.removeCallbacks(hideFavouritesButton);
        if (shakeDetector != null) {
            shakeDetector.unregister();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        initialiseHideButtonRunnables();

        hideButtons(BUTTON_HIDE_TIME);

        if (SettingsManager.getPrefKeepScreenOn(this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (SettingsManager.getPrefShakeForNextProverb(this)) {
            shakeDetector = new ShakeDetector(this, (SensorManager) getSystemService(SENSOR_SERVICE));
        }

        favourites = FavouritesIO.readFavourites(this);
        updateFavouritesMenuItemDrawable();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SLIDESHOW_RUNNING, slideshowRunning);
        outState.putString(SAYING_TEXT, viewPageAdapter.getCurrentSayingText());
        outState.putString(SAYING_IMAGE, viewPageAdapter.getCurrentSayingImageLocation());
        super.onSaveInstanceState(outState);
    }

    private void updateFavouritesMenuItemDrawable() {
        if (menu == null) {
            return;
        }

        menu.findItem(R.id.menu_item_favourites).setIcon(
                favourites.isEmpty() ? android.R.drawable.btn_star : android.R.drawable.btn_star_big_on);
    }

    private void initialiseHideButtonRunnables() {
        hideSlideshowButton = new Runnable() {
            @Override
            public void run() {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    slideShowButton.setAlpha(BUTTON_TRANSPARENCY);
                }
            }
        };

        hideFavouritesButton = new Runnable() {
            @Override
            public void run() {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    favouritesButton.setAlpha(BUTTON_TRANSPARENCY);
                }
            }
        };
    }

    private void hideButtons(int hideTime) {
        handler.postDelayed(hideSlideshowButton, hideTime);
        handler.postDelayed(hideFavouritesButton, hideTime);
    }

    @Override
    public void setSaying(Saying saying) {
        viewPageAdapter.addSaying(saying);

        if (shareActionProvider != null) {
            updateShareIntent();
        }

        imageHandler.loadNextImage(saying.getImageLocation());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        if (favourites != null) {
            updateFavouritesMenuItemDrawable();
        }
        shareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();

        updateShareIntent();
        return true;
    }

    @Override
    public void updateShareIntent() {
        if (shareActionProvider == null) {
            return;
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, viewPageAdapter.getCurrentSayingText() + " - http://proverbica.com");
        shareIntent.setType("text/plain");
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_item_info:
                startActivity(new Intent(this, InfoActivity.class));
                return true;
            case R.id.menu_item_favourites:
                startActivity(new Intent(this, FavouritesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_always_file_key))) {
            if (SettingsManager.getPrefAlwaysUseFile(this)) {
                sayingRetriever = new FileSayingRetriever(this, this);
            } else {
                sayingRetriever = new WebSayingRetriever(this, this);
            }
        }
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
        if (NetworkConnectivity.isNetworkAvailable(this) && !SettingsManager.getPrefAlwaysUseFile(this)) {
            Log.d(TAG, "Loading image");
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
        Log.d(TAG, "Image loaded");
        viewPageAdapter.addBitmap(bitmap);

        int drawable = android.R.drawable.btn_star;
        favouritesButton.setBackgroundTextAndAlpha(drawable, BUTTON_TRANSPARENCY, R.string.add_to_favourites);
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
        Log.d(TAG, "Image failed to load");
    }

    private void toggleSayingIsFavourited() {
        if (favourites.contains(viewPageAdapter.getCurrentSayingText())) {
            favourites.remove(viewPageAdapter.getCurrentSayingText());
        } else {
            favourites.add(viewPageAdapter.getCurrentSayingText());
        }
    }

    @Override
    public void shakeOccurred() {
        sayingRetriever.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
    }

    private void startSlideshow() {
        slideshowRunning = true;
        getActionBar().hide();
        slideShowButton.setBackgroundTextAndAlpha(android.R.drawable.ic_media_pause, 1, R.string.pause_slideshow);
        stopwatch.reset();
        stopwatch.start();
        sayingRetriever.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
        handler.postDelayed(moveToNextImage, 0);
    }

    private void stopSlideshow() {
        slideshowRunning = false;
        getActionBar().show();
        slideShowButton.setBackgroundTextAndAlpha(android.R.drawable.ic_media_play, 1, R.string.play_slideshow);
        handler.removeCallbacks(moveToNextImage);
        stopwatch.reset();
    }

    @Override
    public void updateFavouritesButton(float alpha) {
        int drawable = favourites.contains(viewPageAdapter.getCurrentSayingText()) ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star;
        int text = favourites.contains(viewPageAdapter.getCurrentSayingText()) ? R.string.remove_from_favourites
                : R.string.add_to_favourites;
        favouritesButton.setBackgroundTextAndAlpha(drawable, alpha, text);
    }
}
