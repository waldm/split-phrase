package com.waldm.proverbica.favourites;

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.SimpleAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.waldm.proverbica.R;

public class FavouritesActivity extends ListActivity {
    private class ProverbListItem {
        private String text;
        private boolean isSelected;

        private ProverbListItem(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void negateSelected() {
            isSelected = !isSelected;
        }

        @Override
        public String toString() {
            return getText();
        }
    }

    private static final String PROVERB_KEY = "proverb_key";
    private List<Map<String, ProverbListItem>> list;
    private ShareActionProvider shareActionProvider;
    private SimpleAdapter adapter;
    private Menu menu;

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
            list.add(ImmutableMap.of(PROVERB_KEY, new ProverbListItem(favourite)));
        }

        adapter = new SimpleAdapter(this, list, R.layout.list_item_favourites, new String[] { PROVERB_KEY },
                new int[] { R.id.text });
        setListAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setListAdapter(null);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        list.get(position).get(PROVERB_KEY).negateSelected();
        CheckedTextView checkedView = (CheckedTextView) v;
        checkedView.setChecked(list.get(position).get(PROVERB_KEY).isSelected());
        updateShareIntent();
        updateMenu();
    }

    private void updateMenu() {
        for (Map<String, ProverbListItem> favourite : list) {
            if (favourite.get(PROVERB_KEY).isSelected()) {
                menu.findItem(R.id.menu_item_share).setVisible(true);
                menu.findItem(R.id.menu_item_delete).setVisible(true);
                return;
            }
        }

        menu.findItem(R.id.menu_item_share).setVisible(false);
        menu.findItem(R.id.menu_item_delete).setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourites, menu);
        shareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();

        updateShareIntent();
        this.menu = menu;
        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                deleteSelectedItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteSelectedItems() {
        List<String> toKeep = Lists.newArrayList();
        List<Map<String, ProverbListItem>> toDelete = Lists.newArrayList();
        for (Map<String, ProverbListItem> favourite : list) {
            if (favourite.get(PROVERB_KEY).isSelected()) {
                toDelete.add(favourite);
            } else {
                toKeep.add(favourite.get(PROVERB_KEY).getText());
            }
        }
        for (Map<String, ProverbListItem> favourite : toDelete) {
            list.remove(favourite);
        }

        adapter.notifyDataSetChanged();
        for (int i = 0; i < list.size(); i++) {
            View v = adapter.getView(i, null, null);
            CheckedTextView checkedView = (CheckedTextView) v;
            checkedView.setChecked(false);
        }

        getListView().invalidateViews();

        FavouritesIO.writeFavourites(toKeep, this);
    }

    private void updateShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String favourites = "";
        for (Map<String, ProverbListItem> favourite : list) {
            if (favourite.get(PROVERB_KEY).isSelected()) {
                favourites += favourite.get(PROVERB_KEY).getText() + "\n";
            }
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, favourites);
        shareIntent.setType("text/plain");
        shareActionProvider.setShareIntent(shareIntent);
    }
}
