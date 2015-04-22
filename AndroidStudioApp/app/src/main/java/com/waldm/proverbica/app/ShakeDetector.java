package com.waldm.proverbica.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector implements SensorEventListener {
    public interface Callback {
        void shakeOccurred();
    }

    private static final String TAG = ShakeDetector.class.getSimpleName();

    private static final float SHAKE_THRESHOLD = 800;
    private final Callback callback;
    private float last_x, last_y, last_z;
    private long lastUpdate = -1;
    private SensorManager sensorMgr;

    public ShakeDetector(Callback callback, SensorManager sensorManager) {
        this.callback = callback;
        sensorMgr = sensorManager;
        sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
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

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    Log.d(TAG, "Device was shaken");
                    callback.shakeOccurred();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    public void unregister() {
        if (sensorMgr != null) {
            sensorMgr.unregisterListener(this);
        }
        sensorMgr = null;
    }
}
