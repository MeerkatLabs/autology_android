package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class TelevisionTemplate extends BaseTemplate {

    private static final String TELEVISION = "television";
    private static final String NAME_TAG = "name";
    private static final String SEASON_TAG = "season";
    private static final String EPISODE_TAG = "episode";

    public TelevisionTemplate() {
        super(R.string.template_television_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        Map<String, Object> movieMap = new HashMap<>();
        movieMap.put(NAME_TAG, null);
        movieMap.put(SEASON_TAG, null);
        movieMap.put(EPISODE_TAG, null);

        returnValue.put(TELEVISION, movieMap);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(TELEVISION);

        return returnValue;
    }
}
