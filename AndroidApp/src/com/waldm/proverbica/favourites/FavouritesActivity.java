package com.waldm.proverbica.favourites;

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.waldm.proverbica.R;

public class FavouritesActivity extends ListActivity {
    private class ProverbListItem {
        private String text;
        private boolean isFavourited;

        private ProverbListItem(String text) {
            this.text = text;
            this.isFavourited = true;
        }

        public String getText() {
            return text;
        }

        public boolean isFavourited() {
            return isFavourited;
        }

        public void negateFavourited() {
            isFavourited = !isFavourited;
        }
    }

    private static final String PROVERB_KEY = "proverb_key";
    private List<ProverbListItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> favourites = FavouritesIO.readFavourites(this);
        list = Lists.newArrayList();
        for (String favourite : favourites) {
            list.add(new ProverbListItem(favourite));
        }

        List<Map<String, String>> adapterList = Lists.newArrayList();
        for (ProverbListItem listItem : list) {
            adapterList.add(ImmutableMap.of(PROVERB_KEY, listItem.getText()));
        }

        ListAdapter adapter = new SimpleAdapter(this, adapterList, R.layout.list_item_favourites,
                new String[] { PROVERB_KEY }, new int[] { R.id.text });
        setListAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setListAdapter(null);
        List<String> favourites = Lists.newArrayList();
        for (ProverbListItem proverb : list) {
            if (proverb.isFavourited()) {
                favourites.add(proverb.getText());
            }
        }

        FavouritesIO.writeFavourites(favourites, this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ImageView image = (ImageView) v.findViewById(R.id.image);
        list.get(position).negateFavourited();
        image.setImageResource(list.get(position).isFavourited() ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
    }
}
