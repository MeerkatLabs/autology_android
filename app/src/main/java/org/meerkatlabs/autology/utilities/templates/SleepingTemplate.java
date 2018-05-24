package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class SleepingTemplate extends BaseTemplate {

    private static final String SLEEPING_TAG = "sleeping";

    public SleepingTemplate() {
        super(R.string.template_sleeping_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(SLEEPING_TAG);

        return returnValue;
    }
}
