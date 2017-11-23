package org.meerkatlabs.autology.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

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

    private final Context owner;
    private final static String DEFAULT_AUTOLOGY_DIRECTORY = "autology";
    private final static String DEFAULT_LOGS_DIRECTORY = "logs";

    private static final LogEntry[] EMPTY_ENTRY_ARRAY = {};

    public LogProvider(Context owner) {
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

    public LogEntry[] getFilesList(Calendar currentDate) {

        // If the root directory is not a directory or doesn't exist then return null.
        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            return EMPTY_ENTRY_ARRAY;
        } else if (!logsDirectory.exists() || !logsDirectory.isDirectory()) {
            return EMPTY_ENTRY_ARRAY;
        }

        File yearDirectory = new File(logsDirectory, String.format("%d", currentDate.get(Calendar.YEAR)));

        if (yearDirectory.exists()) {
            File monthDirectory = new File(yearDirectory, String.format("%02d", currentDate.get(Calendar.MONTH)));

            if (monthDirectory.exists()) {
                File dayDirectory = new File(monthDirectory, String.format("%02d", currentDate.get(Calendar.DAY_OF_MONTH)));

                // TODO: Do something
            }
        }

        return EMPTY_ENTRY_ARRAY;
    }

    public File createNewLogFile() {
        return null;
    }

    public interface ILogProvider {
        LogProvider getProvider();
    }

}
