package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class HockeyTemplate extends ExerciseTemplate {

    private static final String HOCKEY_TAG = "hockey";

    public HockeyTemplate() {
        super(R.string.template_hockey_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(HOCKEY_TAG);

        return returnValue;
    }
}
