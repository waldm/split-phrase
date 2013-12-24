package com.waldm.proverbica.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

import com.waldm.proverbica.favourites.FavouritesIO;

public class BackupAgent extends BackupAgentHelper {
    private static final String FAVOURITES_BACKUP_KEY = "FavouritesBackupKey";

    @Override
    public void onCreate() {
        super.onCreate();
        FileBackupHelper fileBackupHelper = new FileBackupHelper(this, FavouritesIO.FAVOURITES_FILENAME);
        addHelper(FAVOURITES_BACKUP_KEY, fileBackupHelper);
    }
}
