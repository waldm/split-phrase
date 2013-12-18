package com.waldm.proverbica.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.waldm.proverbica.R;

public class SettingsManager {

    private SettingsManager() {
    }

    public static boolean getPrefAlwaysUseFile(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.pref_always_file_key), true);
    }

    public static boolean getPrefKeepScreenOn(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.pref_keep_screen_on_key), false);
    }
}
