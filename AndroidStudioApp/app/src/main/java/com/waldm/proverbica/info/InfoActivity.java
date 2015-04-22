package com.waldm.proverbica.info;

import com.waldm.proverbica.BaseActivity;
import com.waldm.proverbica.R;

public class InfoActivity extends BaseActivity {
    @Override
    protected boolean hasParentActivity() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_info;
    }
}
