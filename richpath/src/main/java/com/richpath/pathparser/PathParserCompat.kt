package com.richpath.pathparser

import android.graphics.Path

// This class is a duplicate from the PathParserCompat.java of frameworks/base, with slight
// update on incompatible API like copyOfRange().
object PathParserCompat {

    /**
     * @param endPosition the position of the next separator
     * @param isEndWithNegOrDot whether the next float starts with a '-' or a '.'
     */
    data class ExtractFloatResult(var endPosition: Int = 0,
                                  var isEndWithNegOrDot: Boolean = false)

    // Copy from Arrays.copyOfRange() which is only available from API level 9.
    /**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
     * end (exclusive). The original order of elements is preserved.
     * If {@code end} is greater than {@code original.length}, the result is padded
     * with the value {@code 0.0f}.
     *
     * @param original the original array
     * @param start    the start index, inclusive
     * @param end      the end index, exclusive
     * @return the new array
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0 || start > original.length}
     * @throws IllegalArgumentException       if {@code start > end}
     * @throws NullPointerException           if {@code original == null}
     */
    fun copyOfRange(original: FloatArray, start: Int, end: Int): FloatArray {
        require(start <= end)
        val originalLength = original.size
        if (start < 0 || start > originalLength) {
            throw ArrayIndexOutOfBoundsException()
        }
        val resultLength = end - start
        val copyLength = resultLength.coerceAtMost(originalLength - start)
        val result = FloatArray(resultLength)
        System.arraycopy(original, start, result, 0, copyLength)
        return result
    }

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return the generated Path object.
     */
    fun createPathFromPathData(pathData: String?): Path {
        val path = Path()
        createPathFromPathData(path, pathData)
        return path
    }

    fun createPathFromPathData(path: Path, pathData: String?) {
        createNodesFromPathData(pathData)?.let { nodes ->
            try {
                PathDataNode.nodesToPath(nodes, path)
            } catch (e: RuntimeException) {
                throw RuntimeException("Error in parsing $pathData", e)
            }
        }
    }

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return an array of the PathDataNode.
     */
    fun createNodesFromPathData(pathData: String?): Array<PathDataNode>? {
        pathData ?: return null

        var start = 0
        var end = 1
        val list = arrayListOf<PathDataNode>()
        while (end < pathData.length) {
            end = nextStart(pathData, end)
            val s = pathData.substring(start, end).trim()
            if (s.isNotEmpty()) {
                addNode(list, s[0], getFloats(s))
            }
            start = end
            end++
        }
        if ((end - start) == 1 && start < pathData.length) {
            addNode(list, pathData[start], FloatArray(0))
        }
        return list.toTypedArray()
    }

    /**
     * @param source The array of PathDataNode to be duplicated.
     * @return a deep copy of the <code>source</code>.
     */
    fun deepCopyNodes(source: Array<PathDataNode>): Array<PathDataNode> {
        val copy = arrayListOf<PathDataNode>()
        for (i in source.indices) {
            copy.add(i, PathDataNode(source[i]))
        }
        return copy.toTypedArray()
    }

    /**
     * @param nodesFrom The source path represented in an array of PathDataNode
     * @param nodesTo   The target path represented in an array of PathDataNode
     * @return whether the <code>nodesFrom</code> can morph into <code>nodesTo</code>
     */
    fun canMorph(nodesFrom: Array<PathDataNode>?, nodesTo: Array<PathDataNode>?): Boolean {
        if (nodesFrom == null || nodesTo == null) {
            return false
        }

        if (nodesFrom.size != nodesTo.size) {
            return false
        }

        for (i in nodesFrom.indices) {
            if (nodesFrom[i].type != nodesTo[i].type
                    || nodesFrom[i].params.size != nodesTo[i].params.size) {
                return false
            }
        }
        return true
    }

    /**
     * @param nodes paths represented in an array of an array of PathDataNode
     * @return whether the <code>nodesFrom</code> can morph into <code>nodesTo</code>
     */
    fun canMorph(nodes: Array<Array<PathDataNode>>): Boolean {
        for (pathDataNode in nodes) {
            if (pathDataNode.isEmpty()) {
                return false
            }
        }

        for (i in 0 until nodes.size - 1) {
            if (nodes[i].size != nodes[i + 1].size) {
                return false
            }
        }

        for (i in 0 until nodes.size - 1) {
            for (j in nodes[i].indices) {
                if (nodes[i][j].type != nodes[i + 1][j].type
                        || nodes[i][j].params.size != nodes[i + 1][j].params.size) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Update the target's data to match the source.
     * Before calling this, make sure canMorph(target, source) is true.
     *
     * @param target The target path represented in an array of PathDataNode
     * @param source The source path represented in an array of PathDataNode
     */
    fun updateNodes(target: Array<PathDataNode>, source: Array<PathDataNode>) {
        for (i in source.indices) {
            target[i].type = source[i].type
            for (j in source[i].params.indices) {
                target[i].params[j] = source[i].params[j]
            }
        }
    }

    private fun nextStart(s: String, end: Int): Int {
        var c: Char
        var end = end
        while (end < s.length) {
            c = s[end]
            // Note that 'e' or 'E' are not valid path commands, but could be
            // used for floating point numbers' scientific notation.
            // Therefore, when searching for next command, we should ignore 'e'
            // and 'E'.
            if (((c - 'A') * (c - 'Z') <= 0 || (c - 'a') * (c - 'z') <= 0)
                    && c != 'e' && c != 'E') {
                return end
            }
            end++
        }
        return end
    }

    private fun addNode(list: ArrayList<PathDataNode>, cmd: Char, params: FloatArray) {
        list.add(PathDataNode(cmd, params))
    }

    /**
     * Parse the floats in the string.
     * This is an optimized version of parseFloat(s.split(",|\\s"));
     *
     * @param s the string containing a command and list of floats
     * @return array of floats
     */
    private fun getFloats(s: String): FloatArray {
        if (s[0] == 'z' || s[0] == 'Z') {
            return FloatArray(0)
        }
        try {
            val results = FloatArray(s.length)
            var count = 0
            var startPosition = 1
            var endPosition: Int

            val result = ExtractFloatResult()
            val totalLength = s.length

            // The startPosition should always be the first character of the
            // current number, and endPosition is the character after the current
            // number.
            while (startPosition < totalLength) {
                extract(s, startPosition, result)
                endPosition = result.endPosition

                if (startPosition < endPosition) {
                    results[count++] = s.substring(startPosition, endPosition).toFloat()
                }

                startPosition = if (result.isEndWithNegOrDot) {
                    // Keep the '-' or '.' sign with next number.
                    endPosition
                } else {
                    endPosition + 1
                }
            }
            return copyOfRange(results, 0, count)
        } catch (e: NumberFormatException) {
            throw RuntimeException("error in parsing $s", e)
        }
    }

    /**
     * Calculate the position of the next comma or space or negative sign
     *
     * @param s      the string to search
     * @param start  the position to start searching
     * @param result the result of the extraction, including the position of the
     *               the starting position of next number, whether it is ending with a '-'.
     */
    private fun extract(s: String, start: Int, result: ExtractFloatResult) {
        // Now looking for ' ', ',', '.' or '-' from the start.
        var currentIndex = start
        var foundSeparator = false
        result.isEndWithNegOrDot = false
        var secondDot = false
        var isExponential = false
        while (currentIndex < s.length) {
            val isPrevExponential = isExponential
            isExponential = false
            when (s[currentIndex]) {
                ' ', ',' -> {
                    foundSeparator = true
                }
                '-' -> {
                    // The negative sign following a 'e' or 'E' is not a separator.
                    if (currentIndex != start && !isPrevExponential) {
                        foundSeparator = true
                        result.isEndWithNegOrDot = true
                    }
                }
                '.' -> {
                    if (!secondDot) {
                        secondDot = true
                    } else {
                        // This is the second dot, and it is considered as a separator.
                        foundSeparator = true
                        result.isEndWithNegOrDot = true
                    }
                }
                'e', 'E' -> {
                    isExponential = true
                }
            }
            if (foundSeparator) {
                break
            }
            currentIndex++
        }
        // When there is nothing found, then we put the end position to the end
        // of the string.
        result.endPosition = currentIndex
    }
}