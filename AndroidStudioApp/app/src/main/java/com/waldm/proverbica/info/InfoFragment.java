package com.waldm.proverbica.info;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.waldm.proverbica.R;

public class InfoFragment extends PreferenceFragment {
    private static final String TAG = InfoFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.info);

        // Version
        Activity activity = getActivity();
        String version = "-1";
        try {
            version = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Unable to find version name");
        }
        findPreference(getString(R.string.info_version_key)).setSummary(version);

        findPreference(getString(R.string.info_visit_website_key)).setIntent(
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://proverbica.com")));

        findPreference(getString(R.string.info_twitter_follow_key)).setIntent(
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/proverbica")));

        findPreference(getString(R.string.info_facebook_like_key)).setIntent(
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/Proverbica/617516474977459")));

    }
}
