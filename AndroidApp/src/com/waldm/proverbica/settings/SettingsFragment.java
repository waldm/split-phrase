package com.waldm.proverbica.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.waldm.proverbica.R;

public class SettingsFragment extends PreferenceFragment {
	public static final String KEY_PREF_ALWAYS_USE_FILE = "pref_always_file";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
