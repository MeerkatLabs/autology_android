package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class GroceriesTemplate extends BaseTemplate {

    private static final String GROCERIES_TAG = "groceries";

    public GroceriesTemplate() {
        super(R.string.template_groceries_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(GROCERIES_TAG);

        return returnValue;
    }
}
