package com.waldm.proverbica.settings;

import com.waldm.proverbica.BaseActivity;
import com.waldm.proverbica.R;

public class SettingsActivity extends BaseActivity {
    @Override
    protected boolean hasParentActivity() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }
}
