package com.waldm.proverbica.favourites;

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.waldm.proverbica.R;

public class FavouritesActivity extends ListActivity {
    private static final String PROVERB_KEY = "proverb_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        List<Map<String, String>> list = Lists.newArrayList();
        list.add(ImmutableMap.of(PROVERB_KEY, "Too many cooks spoil the broth"));
        list.add(ImmutableMap.of(PROVERB_KEY, "Too many cooks spoil the tea"));
        list.add(ImmutableMap.of(PROVERB_KEY, "Too many cooks spoil the gin"));
        list.add(ImmutableMap.of(PROVERB_KEY, "Too many cooks spoil the cereal"));

        ListAdapter adapter = new SimpleAdapter(this, list, R.layout.list_item_favourites,
                new String[] { PROVERB_KEY }, new int[] { R.id.text });
        setListAdapter(adapter);
    }
}
