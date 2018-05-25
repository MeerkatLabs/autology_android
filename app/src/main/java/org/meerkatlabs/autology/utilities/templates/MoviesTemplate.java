package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class MoviesTemplate extends BaseTemplate {

    private static final String MOVIES_TAG = "movie";
    private static final String NAME_TAG = "name";
    private static final String YEAR_TAG = "year";

    public MoviesTemplate() {
        super(R.string.template_movies_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        Map<String, Object> movieMap = new HashMap<>();
        movieMap.put(NAME_TAG, null);
        movieMap.put(YEAR_TAG, null);

        returnValue.put(MOVIES_TAG, movieMap);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(MOVIES_TAG);

        return returnValue;
    }
}
