package org.meerkatlabs.autology.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.meerkatlabs.autology.settings.SettingsActivity;
import org.meerkatlabs.autology.utilities.templates.BaseTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

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

                ArrayList<LogEntry> entries = new ArrayList<>();

                // TODO: Actually need to open each of the files and read out the time value in the
                // markdown, but can't really do that yet because I don't have all of the data in place
                // yet.

                for (File entry : dayDirectory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".md");
                    }
                })) {

//                    Log.i("RER", "Found file: " + entry);

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(entry));

                        // detect YAML front matter
                        String line = br.readLine();
                        while (line != null && line.isEmpty()) line = br.readLine();

                        if (line == null) {
                            Log.e("RER", "File is empty");
                            continue;
                        }

                        if (!line.matches("[-]{3,}")) { // use at least three dashes
                            throw new IllegalArgumentException("No YAML Front Matter");
                        }
                        final String delimiter = line;

                        // scan YAML front matter
                        StringBuilder sb = new StringBuilder();
                        line = br.readLine();
                        while (!line.equals(delimiter)) {
                            sb.append(line);
                            sb.append("\n");
                            line = br.readLine();
                        }

//                        Log.i("RER", "Found YAML: " + sb.toString());

                        Yaml yaml = new Yaml();
                        Map<String, Object> frontMatter = yaml.load(sb.toString());

//                        Log.i("RER", "Time Value: " + frontMatter.get("time").getClass());

                        Calendar entryCalendar = Calendar.getInstance();
                        entryCalendar.setTime((Date)frontMatter.get("time"));
                        entries.add(new LogEntry(entryCalendar));

                    } catch(FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                return entries.toArray(new LogEntry[]{});
            }
        }

        return EMPTY_ENTRY_ARRAY;
    }

    public File createNewLogFile(Calendar currentDate, BaseTemplate template) {
        File yearDirectory = new File(logsDirectory, String.format("%d", currentDate.get(Calendar.YEAR)));
        File monthDirectory = new File(yearDirectory, String.format("%02d", currentDate.get(Calendar.MONTH)));
        File dayDirectory = new File(monthDirectory, String.format("%02d", currentDate.get(Calendar.DAY_OF_MONTH)));

        dayDirectory.mkdirs();

        File newFile = new File(dayDirectory, String.format("%tH%tM%tS.md", currentDate, currentDate, currentDate));

        Yaml yaml = new Yaml();


        StringBuffer buffer = new StringBuffer();
        buffer.append("---\n");
        buffer.append(yaml.dump(template.pre()));
        buffer.append("---\n");

        try {
            FileWriter writer = new FileWriter(newFile);
            writer.write(buffer.toString());
            writer.flush();
            Log.i("RER", "Wrote content: " + buffer.toString() + " to file: " + newFile);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFile;
    }

    public interface ILogProvider {
        LogProvider getProvider();
    }

}
