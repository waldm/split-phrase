package com.waldm.proverbica.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.waldm.proverbica.R;
import com.waldm.proverbica.Saying;
import com.waldm.proverbica.SayingDisplayer;
import com.waldm.proverbica.info.InfoActivity;
import com.waldm.proverbica.infrastructure.ImageHandler;
import com.waldm.proverbica.infrastructure.NetworkConnectivity;
import com.waldm.proverbica.infrastructure.SayingSource;
import com.waldm.proverbica.retriever.FileSayingRetriever;
import com.waldm.proverbica.retriever.SayingRetriever;
import com.waldm.proverbica.retriever.WebSayingRetriever;
import com.waldm.proverbica.settings.SettingsActivity;
import com.waldm.proverbica.settings.SettingsManager;

public class MainActivity extends Activity implements OnSharedPreferenceChangeListener, SayingDisplayer, Target,
        SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final float SHAKE_THRESHOLD = 800;
    private SayingRetriever sayingRetriever;
    private ImageHandler imageHandler;
    private String text;
    private TextView textView;
    private ImageView imageView;
    private ShareActionProvider shareActionProvider;
    private SensorManager sensorMgr;
    private float x, y, z;
    private float last_x, last_y, last_z;
    private long lastUpdate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.app_name));

        imageHandler = new ImageHandler(this);
        imageHandler.setTarget(this);

        if (SettingsManager.getPrefAlwaysUseFile(this)) {
            sayingRetriever = new FileSayingRetriever(this, this);
        } else {
            sayingRetriever = new WebSayingRetriever(this, this);
        }

        textView = (TextView) findViewById(R.id.text_box);
        imageView = (ImageView) findViewById(R.id.image);

        addClickListeners();

        // Load first saying
        sayingRetriever = sayingRetriever.loadSayingAndRefresh(SayingSource.EITHER);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void addClickListeners() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sayingRetriever = sayingRetriever.loadSayingAndRefresh(SayingSource.EITHER);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorMgr != null) {
            sensorMgr.unregisterListener(this);
            sensorMgr = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SettingsManager.getPrefKeepScreenOn(this)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (SettingsManager.getPrefShakeForNextProverb(this)) {
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void setSaying(Saying saying) {
        text = saying.getText();
        if (shareActionProvider != null) {
            updateShareIntent();
        }

        imageHandler.loadNextImage(saying.getImageLocation());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        shareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();

        updateShareIntent();
        return true;
    }

    private void updateShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text + " - http://proverbica.com");
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
            textView.setText(R.string.loading_proverb);
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
        Log.d(TAG, "Image loaded");
        imageView.setImageBitmap(bitmap);
        textView.setText(text);
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
        Log.d(TAG, "Image failed to load");
        textView.setText(R.string.failed_to_load_proverb);
    }

    @Override
    public void onAccuracyChanged(Sensor s, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    Log.d(TAG, "Device was shaken");
                    sayingRetriever = sayingRetriever.loadSayingAndRefresh(SayingSource.EITHER);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }
}
