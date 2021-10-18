package com.richpath.pathparser

import android.graphics.Path
import androidx.core.graphics.PathParser

object PathParser {

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return the generated Path object.
     */
    fun createPathFromPathData(pathData: String?): Path {
        return PathParser.createPathFromPathData(pathData) ?: Path()
    }

}