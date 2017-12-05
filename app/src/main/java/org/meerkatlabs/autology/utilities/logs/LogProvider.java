package org.meerkatlabs.autology.utilities.logs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.meerkatlabs.autology.R;
import org.meerkatlabs.autology.settings.SettingsActivity;
import org.meerkatlabs.autology.utilities.templates.BaseTemplate;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
    private final static String DEFAULT_LOGS_DIRECTORY = "log";
    private final static FilenameFilter FILENAME_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".md");
        }
    };

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

        String definedStorageDirectory = preferences.getString(owner.getString(R.string.pref_key__storage_directory), null);
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

        File dayDirectory = getDayDirectory(currentDate);

        if (dayDirectory.exists()) {
            ArrayList<LogEntry> entries = new ArrayList<>();

            for (File entry : dayDirectory.listFiles(FILENAME_FILTER)) {

                LogEntry logEntry = LogEntry.loadLogEntry(entry);
                entries.add(logEntry);

            }

            return entries.toArray(new LogEntry[]{});
        }

        return EMPTY_ENTRY_ARRAY;
    }

    private File getDayDirectory(Calendar currentDate) {
        File yearDirectory = new File(logsDirectory, String.format(Locale.ENGLISH, "%tY", currentDate));
        File monthDirectory = new File(yearDirectory, String.format(Locale.ENGLISH,"%tm", currentDate));
        return new File(monthDirectory, String.format(Locale.ENGLISH,"%td", currentDate));
    }

    public LogEntry createNewLogFile(Calendar currentDate, BaseTemplate template) {
        File dayDirectory = getDayDirectory(currentDate);
        dayDirectory.mkdirs();

        File newFile = new File(dayDirectory, String.format("%tH%tM%tS.md", currentDate, currentDate, currentDate));

        LogEntry entry = LogEntry.createLogEntry(template.pre(currentDate), newFile);
        entry.writeFile();

        return entry;
    }

    public interface ILogProvider {
        LogProvider getProvider();
    }


}
