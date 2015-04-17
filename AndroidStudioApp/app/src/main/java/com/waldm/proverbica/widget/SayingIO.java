package com.waldm.proverbica.widget;

import android.content.Context;
import android.util.Log;

import com.waldm.proverbica.Saying;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SayingIO {
    private static final String TAG = SayingIO.class.getSimpleName();
    private static final String SAYING_SOURCE_WIDGET = "Widget";
    private static final String CURRENT_SAYING_FILENAME = "currentSaying";

    public static void writeSaying(Saying saying, Context context) {
        try {
            FileOutputStream stream = context.openFileOutput(CURRENT_SAYING_FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(SAYING_SOURCE_WIDGET + "\n");
            writer.write(saying.getText() + "\n");
            writer.write(saying.getImageLocation() + "\n");
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to write saying. " + e.toString());
        }
    }

    public static Saying readSaying(Context context) {
        Saying saying = null;
        try {
            FileInputStream stream = context.openFileInput(CURRENT_SAYING_FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            if (reader.readLine().equals(SAYING_SOURCE_WIDGET)) {
                String sayingText = reader.readLine();
                String imageLocation = reader.readLine();
                saying = new Saying(sayingText, imageLocation);
            }
            reader.close();
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to read saying. " + e.toString());
        }

        return saying;
    }
}
