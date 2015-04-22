package com.waldm.proverbica.favourites;

import java.util.List;
import java.util.Map;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.SimpleAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.waldm.proverbica.R;

public class FavouritesFragment extends ListFragment {

    private static final String PROVERB_KEY = "proverb_key";
    private List<Map<String, String>> list;
    private ShareActionProvider shareActionProvider;
    private SimpleAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> favourites = FavouritesIO.readFavourites(getActivity());
        list = Lists.newArrayList();
        for (String favourite : favourites) {
            list.add(ImmutableMap.of(PROVERB_KEY, favourite));
        }

        adapter = new SimpleAdapter(getActivity(), list, R.layout.list_item_favourites, new String[] { PROVERB_KEY },
                new int[] { R.id.text });
        setListAdapter(adapter);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                updateShareIntent();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete:
                        deleteSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.favourites, menu);
                shareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are
                // deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        setListAdapter(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        getListView().setItemChecked(position, true);
        updateShareIntent();
    }

    private void deleteSelectedItems() {
        List<String> toKeep = Lists.newArrayList();
        List<Map<String, String>> toDelete = Lists.newArrayList();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (getListView().isItemChecked(i)) {
                toDelete.add(list.get(i));
            } else {
                toKeep.add(list.get(i).get(PROVERB_KEY));
            }
        }
        for (Map<String, String> favourite : toDelete) {
            list.remove(favourite);
        }

        adapter.notifyDataSetChanged();

        getListView().invalidateViews();

        FavouritesIO.writeFavourites(toKeep, getActivity());
    }

    private void updateShareIntent() {
        if (shareActionProvider == null) {
            return;
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String favourites = "";
        for (int i = 0; i < adapter.getCount(); i++) {
            if (getListView().isItemChecked(i)) {
                favourites += list.get(i).get(PROVERB_KEY) + "\n";
            }
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, favourites);
        shareIntent.setType("text/plain");
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}