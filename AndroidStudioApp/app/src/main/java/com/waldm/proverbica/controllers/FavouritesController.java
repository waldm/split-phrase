package com.waldm.proverbica.controllers;

import android.content.Context;

import com.google.common.collect.Lists;
import com.waldm.proverbica.favourites.FavouritesIO;

import java.util.List;

public class FavouritesController {
    private List<String> favourites = Lists.newArrayList();

    public FavouritesController(Context context){
        readFavourites(context);
    }

    public void readFavourites(Context context){
        favourites = FavouritesIO.readFavourites(context);
    }

    public void saveFavourites(Context context) {
        FavouritesIO.writeFavourites(favourites, context);
    }

    public boolean hasFavourites() {
        return !favourites.isEmpty();
    }

    public void toggle(String favourite) {
        if (hasFavourite(favourite)) {
            favourites.remove(favourite);
        } else {
            favourites.add(favourite);
        }
    }

    public boolean hasFavourite(String favourite) {
        return favourites.contains(favourite);
    }
}
