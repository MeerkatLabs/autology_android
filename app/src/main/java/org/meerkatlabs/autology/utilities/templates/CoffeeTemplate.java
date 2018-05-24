package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.BuildConfig;
import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class CoffeeTemplate extends BaseTemplate {

    private static final String COFFEE_TAG = "coffee";

    public CoffeeTemplate() {
        super(R.string.template_coffe_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        returnValue.put(COFFEE_TAG, 0);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(COFFEE_TAG);

        return returnValue;
    }
}
