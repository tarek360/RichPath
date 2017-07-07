package com.richpathanimator;

import com.richpath.RichPath;

/**
 * Created by tarek on 6/29/17.
 */

public interface AnimationUpdateListener {

    /**
     * Callback method to get the current animated path and the current animated value.
     *
     * @param path  the current animated path
     * @param value the current animated value.
     */
    void update(RichPath path, float value);
}
