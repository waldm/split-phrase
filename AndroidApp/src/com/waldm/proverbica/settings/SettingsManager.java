package com.waldm.proverbica.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager {
    public static final String KEY_PREF_ALWAYS_USE_FILE = "pref_always_file";

    private SettingsManager() {
    }

    public static boolean getPrefAlwaysUseFile(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(KEY_PREF_ALWAYS_USE_FILE, true);
    }
}
