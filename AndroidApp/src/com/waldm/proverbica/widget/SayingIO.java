package com.waldm.proverbica.widget;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

import com.waldm.proverbica.Saying;

public class SayingIO {
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
        } catch (FileNotFoundException e) {} catch (IOException e) {}
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
        } catch (FileNotFoundException e) {} catch (IOException e) {}

        return saying;
    }
}
