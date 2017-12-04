package org.meerkatlabs.autology.utilities.logs;

import android.support.annotation.NonNull;
import android.util.Log;

import org.meerkatlabs.autology.utilities.templates.BaseTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Created by rerobins on 11/21/17.
 */
public class LogEntry {

    private final Map<String, Object> frontmatter;
    private final File logFile;
    private static final String FRONTMATTER_DELIMITER = "[-]{3,}";
    private static final Yaml YAML = new Yaml();

    protected LogEntry(Map<String, Object> _frontmatter, File _logFile) {
        frontmatter = _frontmatter;
        logFile = _logFile.getAbsoluteFile();
    }

    public Calendar getEntryDate() {
        return BaseTemplate.getTime(frontmatter);
    }

    public File getLogFile() {
        return logFile;
    }

    @Override
    public String toString() {
        return String.format("%tR", getEntryDate());
    }

    public void writeFile() {

        StringBuffer buffer = new StringBuffer();
        buffer.append("---\n");
        buffer.append(YAML.dump(frontmatter));
        buffer.append("---\n");

        if (logFile.exists()) {
            buffer.append(loadContent(logFile));
        }

        try {
            FileWriter writer = new FileWriter(logFile);
            writer.write(buffer.toString());
            writer.flush();
            Log.i("RER", "Wrote content: " + buffer.toString() + " to file: " + logFile);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static LogEntry loadLogEntry(@NonNull File logFile) {
        Map<String, Object> frontMatter = loadFrontMatter(logFile);
        return new LogEntry(frontMatter, logFile);
    }

    @NonNull
    private static Map<String, Object> loadFrontMatter(@NonNull File logFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(logFile));

            // detect YAML front matter
            String line = br.readLine();
            while (line != null && line.isEmpty()) line = br.readLine();

            if (line == null) {
                Log.e("RER", "File is empty");
                return Collections.emptyMap();
            }

            if (!line.matches(FRONTMATTER_DELIMITER)) { // use at least three dashes
                throw new IllegalArgumentException("No YAML Front Matter");
            }
            final String delimiter = line;

            // scan YAML front matter
            StringBuilder sb = new StringBuilder();
            line = br.readLine();
            while (!line.equals(delimiter)) {
                sb.append(line).append('\n');
                line = br.readLine();
            }

            Yaml yaml = new Yaml();
            return yaml.load(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyMap();
    }

    @NonNull
    private static String loadContent(@NonNull File logFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(logFile));

            // detect YAML front matter
            String line = br.readLine();
            while (line != null && line.isEmpty()) line = br.readLine();

            if (line == null) {
                Log.e("RER", "File is empty");
                return "";
            }

            if (!line.matches(FRONTMATTER_DELIMITER)) { // use at least three dashes
                throw new IllegalArgumentException("No YAML Front Matter");
            }
            final String delimiter = line;

            // scan YAML front matter

            line = br.readLine();
            while (!line.equals(delimiter)) {
                line = br.readLine();
            }

            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append('\n');
                line = br.readLine();
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @NonNull
    public static LogEntry createLogEntry(@NonNull Map<String, Object> frontMatter, @NonNull File content) {

        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime((Date) frontMatter.get("time"));
        return new LogEntry(frontMatter, content);

    }

    public static interface ILogEntryEditor {
        void editLogEntry(LogEntry entry);
    }

}