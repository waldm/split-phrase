package com.waldm.proverbica.favourites;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import android.app.backup.BackupManager;
import android.content.Context;
import android.util.Log;

import com.google.common.collect.Lists;

public class FavouritesIO {
    public static final String FAVOURITES_FILENAME = "favourites";
    private static final String TAG = FavouritesIO.class.getSimpleName();

    public static void writeFavourites(List<String> favourites, Context context) {
        Log.d(TAG, "Writing favourites");
        try {
            FileOutputStream stream = context.openFileOutput(FAVOURITES_FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            for (String favourite : favourites) {
                writer.write(favourite + "\n");
            }
            writer.close();
            Log.d(TAG, "Backing up favourites to the cloud");
            BackupManager backupManager = new BackupManager(context);
            backupManager.dataChanged();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to write favourites, file not found");
        } catch (IOException e) {
            Log.e(TAG, "Failed to write favourites");
        }
    }

    public static List<String> readFavourites(Context context) {
        Log.d(TAG, "Reading favourites");
        List<String> favourites = Lists.newArrayList();
        try {
            FileInputStream stream = context.openFileInput(FAVOURITES_FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String favourite;
            while ((favourite = reader.readLine()) != null) {
                favourites.add(favourite);
            }
            reader.close();
            stream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to read favourites, file not found");
        } catch (IOException e) {
            Log.e(TAG, "Failed to read favourites");
        }

        return favourites;
    }
}
