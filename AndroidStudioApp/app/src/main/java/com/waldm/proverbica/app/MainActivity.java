package com.waldm.proverbica.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.common.base.Stopwatch;
import com.waldm.proverbica.R;
import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.app.ShakeDetector.Callback;
import com.waldm.proverbica.controllers.SayingController;
import com.waldm.proverbica.favourites.FavouritesActivity;
import com.waldm.proverbica.favourites.FavouritesIO;
import com.waldm.proverbica.info.InfoActivity;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.FileSayingRetriever;
import com.waldm.proverbica.retriever.SayingRetriever;
import com.waldm.proverbica.retriever.WebSayingRetriever;
import com.waldm.proverbica.settings.SettingsActivity;
import com.waldm.proverbica.settings.SettingsManager;
import com.waldm.proverbica.views.ProverbicaButton;
import com.waldm.proverbica.widget.SayingIO;
import com.waldm.proverbica.widget.UpdateWidgetService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener, SayingDisplayer,
        Callback {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final float BUTTON_TRANSPARENCY = 0.3f;
    protected static final long SLIDESHOW_TRANSITION = 3000;
    private static final int BUTTON_HIDE_TIME = 2000;

    private static final String SLIDESHOW_RUNNING = "SLIDESHOW_RUNNING";
    private static final String SAYING_TEXT = "SayingText";
    private static final String SAYING_IMAGE = "SayingImage";
    private static final String WALDM = "WALDM";
    private ImageView imageView;
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
    private Button previousButton;
    private Button nextButton;
    private TextView textView;
    private SayingController sayingController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(WALDM, "onCreate: " +  savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.app_name));

        moveToNextImage = new Runnable() {
            @Override
            public void run() {
                if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > SLIDESHOW_TRANSITION) {
                    sayingController.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
                    stopwatch.reset();
                    stopwatch.start();
                }

                handler.postDelayed(moveToNextImage, SLIDESHOW_TRANSITION);
            }
        };

        SayingRetriever sayingRetriever;
        if (SettingsManager.getPrefAlwaysUseFile(this)) {
            sayingRetriever = new FileSayingRetriever(this);
        } else {
            sayingRetriever = new WebSayingRetriever(this);
        }

        sayingController = new SayingController(this, this, sayingRetriever);

        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text_box);
        slideShowButton = (ProverbicaButton) findViewById(R.id.button_slideshow);
        favouritesButton = (ProverbicaButton) findViewById(R.id.button_favourite);
        previousButton = (Button) findViewById(R.id.previous_button);
        nextButton = (Button) findViewById(R.id.next_button);

        addClickListeners();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (savedInstanceState == null) {
            if (!tryLoadSayingFromWidget(getIntent())) {
                sayingController.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
            }
        } else {
            slideshowRunning = savedInstanceState.getBoolean(SLIDESHOW_RUNNING);
            sayingController.setSaying(new Saying(savedInstanceState.getString(SAYING_TEXT), savedInstanceState.getString(SAYING_IMAGE)));

            if (slideshowRunning) {
                startSlideshow();
            }
        }
    }

    private boolean tryLoadSayingFromWidget(Intent intent) {
        Log.e(WALDM, "tryLoadSayingFromWidget");
        if (intent.getBooleanExtra(UpdateWidgetService.EXTRA_STARTED_VIA_WIDGET, false)) {
            // Load saying currently being shown in widget
            Saying saying = SayingIO.readSaying(this);
            if (saying != null) {
                sayingController.setSaying(saying);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e(WALDM,"oneNewIntent");
        super.onNewIntent(intent);
        tryLoadSayingFromWidget(intent);
    }

    private void addClickListeners() {
        Log.e(WALDM, "addClickListeners");
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

        previousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sayingController.displayPreviousSaying();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sayingController.displayNextSaying();
            }
        });
    }

    @Override
    protected void onPause() {
        Log.e(WALDM, "onPause");
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
        Log.e(WALDM, "onResume");
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e(WALDM, "onSaveInstanceState");
        outState.putBoolean(SLIDESHOW_RUNNING, slideshowRunning);
        outState.putString(SAYING_TEXT, sayingController.getCurrentSaying().getText());
        outState.putString(SAYING_IMAGE, sayingController.getCurrentSaying().getImageLocation());
        super.onSaveInstanceState(outState);
    }

    private void updateFavouritesMenuItemDrawable() {
        Log.e(WALDM, "updateFavouritesMenuItemDrawable");
        if (menu == null) {
            return;
        }

        menu.findItem(R.id.menu_item_favourites).setIcon(
                favourites.isEmpty() ? android.R.drawable.btn_star : android.R.drawable.btn_star_big_on);
    }

    private void initialiseHideButtonRunnables() {
        Log.e(WALDM, "initialiseHideButtonRunnables");
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
        Log.e(WALDM, "hideButtons");
        handler.postDelayed(hideSlideshowButton, hideTime);
        handler.postDelayed(hideFavouritesButton, hideTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(WALDM, "onCreateOptionsMenu");
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
        Log.e(WALDM, "updateShareIntent");
        if (shareActionProvider == null) {
            return;
        }

        Saying currentSaying = sayingController.getCurrentSaying();
        if (currentSaying != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentSaying.getText() + " - http://proverbica.com");
            shareIntent.setType("text/plain");
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void displaySaying(Saying currentSaying, Bitmap bitmap, boolean canGoBack) {
        Log.e(WALDM, "displaySaying");
        textView.setText(currentSaying.getText());
        imageView.setImageBitmap(bitmap);

        int drawable = android.R.drawable.btn_star;
        favouritesButton.setBackgroundTextAndAlpha(drawable, BUTTON_TRANSPARENCY, R.string.add_to_favourites);

        if (!slideshowRunning) {
            previousButton.setVisibility(canGoBack ? View.VISIBLE : View.INVISIBLE);
        }
        updateShareIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(WALDM, "onOptionsItemSelected");
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
        Log.e(WALDM, "onSharedPreferenceChanged");
        if (key.equals(getString(R.string.pref_always_file_key))) {
            if (SettingsManager.getPrefAlwaysUseFile(this)) {
                sayingController.setSayingRetriever(new FileSayingRetriever(this));
            } else {
                sayingController.setSayingRetriever(new WebSayingRetriever(this));
            }
        }
    }

    private void toggleSayingIsFavourited() {
        Log.e(WALDM, "toggleSayingIsFavourited");
        String currentSayingText = sayingController.getCurrentSaying().getText();
        if (favourites.contains(currentSayingText)) {
            favourites.remove(currentSayingText);
        } else {
            favourites.add(currentSayingText);
        }
    }

    @Override
    public void shakeOccurred() {
        Log.e(WALDM, "shakeOccurred");
        sayingController.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
    }

    private void startSlideshow() {
        Log.e(WALDM, "startSlideshow");
        slideshowRunning = true;
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        slideShowButton.setBackgroundTextAndAlpha(android.R.drawable.ic_media_pause, 1, R.string.pause_slideshow);
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);

        stopwatch.reset();
        stopwatch.start();
        sayingController.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
        handler.postDelayed(moveToNextImage, 0);
    }

    private void stopSlideshow() {
        Log.e(WALDM, "stopSlideshow");
        slideshowRunning = false;
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.show();
        }

        slideShowButton.setBackgroundTextAndAlpha(android.R.drawable.ic_media_play, 1, R.string.play_slideshow);
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);

        handler.removeCallbacks(moveToNextImage);
        stopwatch.reset();
    }

    @Override
    public void updateFavouritesButton(float alpha) {
        Log.e(WALDM, "updateFavouritesButton");
        String currentSayingText = sayingController.getCurrentSaying().getText();
        int drawable = favourites.contains(currentSayingText) ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star;
        int text = favourites.contains(currentSayingText) ? R.string.remove_from_favourites
                : R.string.add_to_favourites;
        favouritesButton.setBackgroundTextAndAlpha(drawable, alpha, text);
    }
}
