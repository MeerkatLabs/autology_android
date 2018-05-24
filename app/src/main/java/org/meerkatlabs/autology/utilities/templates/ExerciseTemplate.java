package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class ExerciseTemplate extends BaseTemplate {

    private static final String EXERCISE_TAG = "exercise";

    public ExerciseTemplate() {
        super(R.string.template_exercise_name);
    }

    protected ExerciseTemplate(int i) { super(i); }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(EXERCISE_TAG);

        return returnValue;
    }
}
