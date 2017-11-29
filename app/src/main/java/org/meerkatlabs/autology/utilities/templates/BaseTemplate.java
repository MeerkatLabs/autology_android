package org.meerkatlabs.autology.utilities.templates;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Template that provides core functionality for recording times.
 */
public class BaseTemplate implements Comparable<BaseTemplate> {

    private final String name;

    public BaseTemplate(){
        this("Base Template");
    }

    public BaseTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Object> pre() {
        Map<String, Object> returnValue = new HashMap<>();

        returnValue.put("time", new Date());
        returnValue.put("end_time", null);

        return returnValue;
    }

    public Map<String, Object> post(@NonNull Map<String, Object> incoming) {

        incoming.put("end_time", new Date());

        return incoming;
    }

    @Override
    public int compareTo(@NonNull BaseTemplate o) {
        return name.compareTo(o.getName());
    }
}
