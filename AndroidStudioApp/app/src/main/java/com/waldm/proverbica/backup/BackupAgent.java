package com.waldm.proverbica.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

import com.waldm.proverbica.favourites.FavouritesIO;

public class BackupAgent extends BackupAgentHelper {
    private static final String FAVOURITES_BACKUP_KEY = "FavouritesBackupKey";
    private static final String PREFERENCES_BACKUP_KEY = "PreferencesBackupKey";
    private static final String DEFAULT_PREFERENCES = "com.waldm.proverbica_preferences";

    @Override
    public void onCreate() {
        super.onCreate();
        FileBackupHelper fileBackupHelper = new FileBackupHelper(this, FavouritesIO.FAVOURITES_FILENAME);
        addHelper(FAVOURITES_BACKUP_KEY, fileBackupHelper);

        SharedPreferencesBackupHelper preferencesBackupHelper = new SharedPreferencesBackupHelper(this,
                DEFAULT_PREFERENCES);
        addHelper(PREFERENCES_BACKUP_KEY, preferencesBackupHelper);
    }
}
