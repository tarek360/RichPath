package com.richpath.pathparser

import android.graphics.Path
import android.os.Build

object PathParser {

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return the generated Path object.
     */
    fun createPathFromPathData(pathData: String?): Path {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PathParserCompatApi21.createPathFromPathData(pathData) ?: Path()
        } else {
            PathParserCompat.createPathFromPathData(pathData)
        }
    }

}