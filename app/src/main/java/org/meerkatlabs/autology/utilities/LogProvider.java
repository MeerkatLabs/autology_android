package org.meerkatlabs.autology.utilities;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.meerkatlabs.autology.settings.SettingsActivity;

import java.io.File;
import java.util.Calendar;

/**
 * Provider that will define how to access the logs that are stored in the storage area.
 *
 * For this implementation, all of the logs are stored with in a 'logs' directory inside the
 * storage directory defined in settings.  It is lazy, it will not create data directories or files
 * until necessary (i.e. reading empty will just be a null list, until the directory needs to be
 * created).
 */
public class LogProvider {

    private File rootDirectory = null;
    private File logsDirectory = null;

    private final AppCompatActivity owner;
    private final String DEFAULT_AUTOLOGY_DIRECTORY = "autology";
    private final String DEFAULT_LOGS_DIRECTORY = "logs";

    public LogProvider(AppCompatActivity owner) {
        this.owner = owner;
    }

    /**
     * Load the configuration values from the shared resources and attempt to look up the data
     * stored in the logs directory.
     */
    public void initialize() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(owner);

        String definedStorageDirectory = preferences.getString(SettingsActivity.STORAGE_DIRECTORY_KEY, null);
        if (definedStorageDirectory == null) {
            rootDirectory = new File(Environment.getExternalStorageDirectory(), DEFAULT_AUTOLOGY_DIRECTORY);
        } else {
            rootDirectory = new File(definedStorageDirectory);
        }

        logsDirectory = new File(rootDirectory, DEFAULT_LOGS_DIRECTORY);
    }

    public LogEntry[] getFilesList() {

        return new LogEntry[] {
                new LogEntry(Calendar.getInstance()),
        };
    }

}
