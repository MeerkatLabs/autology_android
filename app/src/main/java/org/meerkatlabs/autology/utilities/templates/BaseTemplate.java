package org.meerkatlabs.autology.utilities.templates;

import android.support.annotation.NonNull;

import org.meerkatlabs.autology.R;
import org.meerkatlabs.autology.BuildConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Template that provides core functionality for recording times.
 */
public class BaseTemplate implements Comparable<BaseTemplate> {

    protected static final String TIME_TAG = "time";
    protected static final String END_TIME_TAG = "end_time";
    protected static final String AGENT_DEFINITION = "autology_agent";
    protected static final String AGENT_NAME = "name";
    protected static final String AGENT_VERSION = "version";
    protected static final String FILE_VERSION = "file_definition";
    protected static final String ACTIVITIES_TAG = "activities";
    protected static final String LOCATION_TAG = "location";

    private final int nameResource;

    public BaseTemplate() {
        this(R.string.template_base_name);
    }

    public BaseTemplate(int nameResource) {
        this.nameResource = nameResource;
    }

    public int getNameResource() {
        return this.nameResource;
    }

    public Map<String, Object> pre(Calendar dateTime) {

        if (dateTime == null) {
            dateTime = Calendar.getInstance();
        }

        Map<String, Object> returnValue = new HashMap<>();

        returnValue.put(TIME_TAG, dateTime.getTime());
        returnValue.put(END_TIME_TAG, null);

        returnValue.put(ACTIVITIES_TAG, new ArrayList<String>());

        Map<String, Object> agentData = new HashMap<>();
        agentData.put(AGENT_VERSION, BuildConfig.VERSION_NAME);
        agentData.put(FILE_VERSION, BuildConfig.FILE_VERSION);
        agentData.put(AGENT_NAME, BuildConfig.APPLICATION_ID);

        returnValue.put(AGENT_DEFINITION, agentData);

        returnValue.put(LOCATION_TAG, null);

        return returnValue;
    }

    public Map<String, Object> post(@NonNull Map<String, Object> incoming) {
        // Only update the end_time if it hasn't already been set.
        if (!incoming.containsKey(END_TIME_TAG) || incoming.get(END_TIME_TAG) == null) {
            incoming.put(END_TIME_TAG, new Date());
        }
        return incoming;
    }

    @Override
    public int compareTo(@NonNull BaseTemplate o) {
        return Integer.compare(nameResource, o.getNameResource());
    }

    public static Calendar getTime(@NonNull Map<String, Object> frontMatter) {
        return getTimeValue(frontMatter, TIME_TAG);
    }

    public static Calendar getEndTime(@NonNull Map<String, Object> frontMatter) {
        return getTimeValue(frontMatter, END_TIME_TAG);
    }

    private static Calendar getTimeValue(@NonNull Map<String, Object> frontMatter, @NonNull  String tag) {
        if (frontMatter.containsKey(tag)) {
            Object timeValue = frontMatter.get(tag);
            if (timeValue != null) {
                try {
                    Calendar entryDate = Calendar.getInstance();
                    entryDate.setTime((Date) timeValue);
                    return entryDate;
                } catch (ClassCastException e) {
                    return null;
                }
            }
        }

        return null;
    }
}
