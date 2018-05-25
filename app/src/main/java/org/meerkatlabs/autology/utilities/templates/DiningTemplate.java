package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class DiningTemplate extends BaseTemplate {

    private static final String DINING_TAG = "dining";

    public DiningTemplate() {
        super(R.string.template_dining_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(DINING_TAG);

        return returnValue;
    }
}
