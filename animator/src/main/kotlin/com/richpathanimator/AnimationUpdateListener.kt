package com.richpathanimator

import richpath.RichPath

interface AnimationUpdateListener {

    /**
     * Callback method to get the current animated path and the current animated value.
     *
     * @param path  the current animated path
     * @param value the current animated value.
     */
    fun update(path: RichPath, value: Float)
}
