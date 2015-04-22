package com.waldm.proverbica.favourites;

import com.waldm.proverbica.BaseActivity;
import com.waldm.proverbica.R;

public class FavouritesActivity extends BaseActivity{
    @Override
    protected boolean hasParentActivity() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_favourites;
    }
}
