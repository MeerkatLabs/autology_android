package org.meerkatlabs.autology.utilities.templates;

import org.meerkatlabs.autology.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coffee template for paper cuts, this class should not extend past this branch.
 */

public class VideoGameTemplate extends BaseTemplate {

    private static final String VIDEO_GAME_TAG = "video_game";
    private static final String NAME_TAG = "name";

    public VideoGameTemplate() {
        super(R.string.template_video_games_name);
    }

    public Map<String, Object> pre(Calendar dateTime) {

        Map<String, Object> returnValue = super.pre(dateTime);

        Map<String, Object> movieMap = new HashMap<>();
        movieMap.put(NAME_TAG, null);

        returnValue.put(VIDEO_GAME_TAG, movieMap);

        List<String> activities = (List<String>) returnValue.get(ACTIVITIES_TAG);
        activities.add(VIDEO_GAME_TAG);

        return returnValue;
    }
}
