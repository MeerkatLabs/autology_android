package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class WeightTemplate extends BaseTemplate {

    private static final String WEIGHT_TAG = "weight";

    public WeightTemplate() {
        super(R.string.template_weight_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        returnValue.put(WEIGHT_TAG, 0);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(WEIGHT_TAG);

        return returnValue;
    }
}
