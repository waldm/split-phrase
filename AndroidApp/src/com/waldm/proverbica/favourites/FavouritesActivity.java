package com.waldm.proverbica.favourites;

import android.app.ListActivity;
import android.os.Bundle;

import com.waldm.proverbica.R;

public class FavouritesActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
    }
}
