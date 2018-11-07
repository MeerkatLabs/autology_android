package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class ProjectTemplate extends BaseTemplate {

    private static final String PROJECT_TAG = "project";

    public ProjectTemplate() {
        super(R.string.template_project_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        returnValue.put(PROJECT_TAG, 0);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(PROJECT_TAG);

        return returnValue;
    }
}
