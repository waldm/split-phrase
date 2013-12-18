package com.waldm.proverbica.info;

import android.app.Activity;
import android.os.Bundle;

public class InfoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new InfoFragment()).commit();
    }
}
