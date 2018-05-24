package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class AlcoholTemplate extends BaseTemplate {

    private static final String ALCOHOL_TAG = "alcohol";
    private static final String BEER_TAG = "beer";
    private static final String WINE_TAG = "wine";

    public AlcoholTemplate() {
        super(R.string.template_alcohol_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        Map<String, Object> alcoholMap = new HashMap<>();
        alcoholMap.put(BEER_TAG, 0);
        alcoholMap.put(WINE_TAG, 0);

        returnValue.put(ALCOHOL_TAG, alcoholMap);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(ALCOHOL_TAG);

        return returnValue;
    }
}
