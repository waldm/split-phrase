package com.waldm.proverbica.favourites;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import android.content.Context;

import com.google.common.collect.Lists;

public class FavouritesIO {
    private static final String FAVOURITES_FILENAME = "favourites";

    public static void writeFavourites(List<String> favourites, Context context) {
        try {
            FileOutputStream stream = context.openFileOutput(FAVOURITES_FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            for (String favourite : favourites) {
                writer.write(favourite + "\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {} catch (IOException e) {}
    }

    public static List<String> readFavourites(Context context) {
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
        } catch (FileNotFoundException e) {} catch (IOException e) {}

        return favourites;
    }
}
